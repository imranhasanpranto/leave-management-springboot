package com.enosis.leavemanagement.service;

import com.enosis.leavemanagement.dto.LeaveApplicationDTO;
import com.enosis.leavemanagement.dto.LeaveDaysDTO;
import com.enosis.leavemanagement.dto.UserLeaveApplicationDTO;
import com.enosis.leavemanagement.enums.ApplicationStatus;
import com.enosis.leavemanagement.enums.Role;
import com.enosis.leavemanagement.exceptions.FileSaveException;
import com.enosis.leavemanagement.interfaces.ProjectDateRange;
import com.enosis.leavemanagement.model.LeaveApplication;
import com.enosis.leavemanagement.model.LeaveApplicationDtoToModelMapper;
import com.enosis.leavemanagement.model.UserLeaveCount;
import com.enosis.leavemanagement.model.Users;
import com.enosis.leavemanagement.repository.LeaveRepository;
import com.enosis.leavemanagement.utils.DateUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveService {
    private final LeaveRepository leaveRepository;
    private final FileService fileService;
    private final UserService userService;
    private final UserLeaveCountService userLeaveCountService;
    private final UserLeaveDaysService userLeaveDaysService;

    public LeaveDaysDTO updateAnnualLeavesCount(LeaveApplication leaveApplication, Long userId){
        LeaveDaysDTO leaveDaysDTO = getLeaveCount(leaveApplication.getFromDate().toLocalDate(), leaveApplication.getToDate().toLocalDate(), userId, leaveApplication.getId());
        UserLeaveCount balance = userLeaveCountService.getLeaveBalance(userId);
        int currentBalance = balance.getValue() + leaveApplication.getLeaveCount() - leaveDaysDTO.getLeaveCount();
        if(currentBalance < 0){
            throw new IllegalArgumentException("Annual Leave Count Exceeded!");
        }
        userLeaveCountService.updateLeaveCountBalance(userId, currentBalance);

        return leaveDaysDTO;
    }

    @Transactional
    public String updateLeaveApplication(LeaveApplicationDTO leaveApplicationDTO, Long userId){
        Optional<LeaveApplication> leaveApplicationOptional = leaveRepository.findById(leaveApplicationDTO.getId());
        if(leaveApplicationOptional.isPresent()){
            LeaveApplication leaveApplication = leaveApplicationOptional.get();

            LeaveDaysDTO leaveDaysDTO = updateAnnualLeavesCount(leaveApplication, userId);

            String path = leaveApplication.getFilePath();
            Boolean isFileUpdated = leaveApplicationDTO.getIsFileUpdated();
            if(Boolean.TRUE.equals(isFileUpdated)){
                path = fileService.fileUpdate(leaveApplicationDTO, leaveApplication.getFilePath(), userId);
            }

            leaveApplication.setUserId(userId);
            leaveApplication.setFilePath(path);
            leaveApplication.setApplicationStatus(ApplicationStatus.Pending);

            LeaveApplicationDtoToModelMapper modelMapper = new LeaveApplicationDtoToModelMapper();
            modelMapper.reMapDtoToModel(leaveApplicationDTO, leaveApplication, leaveDaysDTO.getLeaveCount());

            userLeaveDaysService.deleteByApplicationId(leaveApplication.getId());
            userLeaveDaysService.saveAllByDateList(leaveDaysDTO.getLeaveDays(), leaveApplication.getId());
        }
        return "Leave request updated successfully";
    }

    @Transactional
    public String saveLeaveApplication(LeaveApplicationDTO leaveApplicationDTO, Long userId) throws IllegalArgumentException, FileSaveException{
        LeaveApplicationDtoToModelMapper modelMapper = new LeaveApplicationDtoToModelMapper();
        LeaveApplication leaveApplication = modelMapper.mapDtoToModel(leaveApplicationDTO);
        //updating annual leave count
        LeaveDaysDTO leaveDaysDTO = getLeaveCount(leaveApplication.getFromDate().toLocalDate(), leaveApplication.getToDate().toLocalDate(), userId, -1l);
        UserLeaveCount balance = userLeaveCountService.getLeaveBalance(userId);
        int currentBalance = balance.getValue() - leaveDaysDTO.getLeaveCount();
        if(currentBalance < 0){
            throw new IllegalArgumentException("Annual Leave Count Exceeded!");
        }

        String path = "";
        if(leaveApplicationDTO.getFile() != null) {
            path = fileService.saveFile(leaveApplicationDTO.getFile(), userId);
        }

        userLeaveCountService.updateLeaveCountBalance(userId, currentBalance);

        leaveApplication.setLeaveCount(leaveDaysDTO.getLeaveCount());
        leaveApplication.setFilePath(path);
        leaveApplication.setApplicationStatus(ApplicationStatus.Pending);
        leaveApplication.setUserId(userId);
        leaveApplication = leaveRepository.save(leaveApplication);

        userLeaveDaysService.saveAllByDateList(leaveDaysDTO.getLeaveDays(), leaveApplication.getId());

        return "Leave request added successfully";
    }

    public LeaveDaysDTO getLeaveCount(LocalDate fromDate, LocalDate toDate, Long userId, Long id){

        List<LocalDate> blockedDates = getAllLeaveDates(userId, id);
        Set<Long> blockedDateSet = new HashSet<>();
        for(LocalDate date: blockedDates){
            blockedDateSet.add(date.toEpochDay());
        }

        DateUtils utils = new DateUtils();

        return utils.getAllLeaveDates(fromDate, toDate, blockedDateSet);
    }

    @Transactional
    public LeaveApplication updateRequestStatus(Long id, ApplicationStatus status){
        Optional<LeaveApplication> leaveApplicationOptional = leaveRepository.findById(id);
        if(leaveApplicationOptional.isPresent()){
            LeaveApplication application = leaveApplicationOptional.get();
            application.setApplicationStatus(status);
            if(ApplicationStatus.Rejected.equals(status) || ApplicationStatus.Canceled.equals(status)){
                userLeaveCountService.updateLeaveCountAfterDeleteApplication(application.getUserId(), application.getLeaveCount());
            }
            return application;
        }
        return null;
    }


    public LeaveApplication getById(Long id){
        Optional<LeaveApplication> leaveApplicationOptional = leaveRepository.findById(id);
        if(leaveApplicationOptional.isPresent()){
            LeaveApplication leaveApplication =  leaveApplicationOptional.get();

            //fetch file
            if(leaveApplication.getFilePath() != null && !"".equals(leaveApplication.getFilePath())){
                Path imagePath = Paths.get("uploads/"+leaveApplication.getFilePath());
                try {
                    leaveApplication.setAttachment(Files.readAllBytes(imagePath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            return leaveApplication;
        }
        return new LeaveApplication();
    }

    public List<UserLeaveApplicationDTO> getAllPendingLeaveRequests(String userEmail){
        Optional<Users> usersOptional = userService.findByEmail(userEmail);
        List<UserLeaveApplicationDTO> applicationList;
        if(usersOptional.isPresent()){
            Users users = usersOptional.get();
            if(Role.Admin.equals(users.getRole())){
                applicationList = leaveRepository.findByApplicationStatusOrderByIdDesc(ApplicationStatus.Pending);
            }else {
                applicationList = leaveRepository.findByUserIdAndApplicationStatusOrderByIdDesc(users.getId(), ApplicationStatus.Pending);
            }
        }else{
            applicationList = new ArrayList<>();
        }
        return applicationList;
    }

    public List<UserLeaveApplicationDTO> getAllApprovedLeaveRequests(){
        return leaveRepository.findByApplicationStatusOrderByIdDesc(ApplicationStatus.Approved);
    }

    public List<UserLeaveApplicationDTO> getApprovedListByName(String name){
        return leaveRepository.findApprovedListByName(name, ApplicationStatus.Approved);
    }

    public List<LocalDate> getAllLeaveDates(Long userId, Long id){
        List<ProjectDateRange> leaveDatesRange = leaveRepository.findByUserIdAndIdNotAndApplicationStatusIn(
                userId,
                id,
                List.of(ApplicationStatus.Pending, ApplicationStatus.Approved)
        );

        List<LocalDate> list = new ArrayList<>();
        for(ProjectDateRange range: leaveDatesRange) {
            list.addAll(range.getFromDate().toLocalDate().datesUntil(range.getToDate().toLocalDate())
                    .toList());
            list.add(range.getToDate().toLocalDate());
        }
        return list;
    }

    public int getLeaveCountBalance(
            Long fromDateMil,
            Long toDateMil,
            Long userId,
            Long id
    ){
        LocalDate fromDate = Instant.ofEpochMilli(fromDateMil).atZone(ZoneId.of("UTC")).toLocalDate();
        LocalDate toDate = Instant.ofEpochMilli(toDateMil).atZone(ZoneId.of("UTC")).toLocalDate();

        LeaveDaysDTO leaveDaysDTO = getLeaveCount(fromDate, toDate, userId, id);
        UserLeaveCount balance = userLeaveCountService.getLeaveBalance(userId);
        return balance.getValue() - leaveDaysDTO.getLeaveCount();
    }


}
