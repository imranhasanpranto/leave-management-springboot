package com.enosis.leavemanagement.service;

import com.enosis.leavemanagement.dto.LeaveApplicationDTO;
import com.enosis.leavemanagement.dto.LeaveDaysDTO;
import com.enosis.leavemanagement.dto.UserLeaveApplicationDTO;
import com.enosis.leavemanagement.enums.ApplicationStatus;
import com.enosis.leavemanagement.enums.Role;
import com.enosis.leavemanagement.exceptions.FileSaveException;
import com.enosis.leavemanagement.interfaces.ProjectDateRange;
import com.enosis.leavemanagement.model.LeaveApplication;
import com.enosis.leavemanagement.model.UserLeaveCount;
import com.enosis.leavemanagement.model.Users;
import com.enosis.leavemanagement.repository.LeaveRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveService {
    private final LeaveRepository leaveRepository;
    private final FileService fileService;
    private final UserService userService;
    private final UserLeaveCountService userLeaveCountService;
    private final UserLeaveDaysService userLeaveDaysService;

    @Transactional
    public String updateLeaveApplication(LeaveApplicationDTO leaveApplicationDTO, Long userId) throws IllegalArgumentException, FileSaveException{
        Optional<LeaveApplication> leaveApplicationOptional = leaveRepository.findById(leaveApplicationDTO.getId());
        if(leaveApplicationOptional.isPresent()){
            LeaveApplication leaveApplication = leaveApplicationOptional.get();

            //updating annual leave count
            LeaveDaysDTO leaveDaysDTO = getLeaveCount(leaveApplication.getFromDate().toLocalDate(), leaveApplication.getToDate().toLocalDate(), userId, leaveApplication.getId());
            UserLeaveCount balance = userLeaveCountService.getLeaveBalance(userId);
            int currentBalance = balance.getValue() + leaveApplication.getLeaveCount() - leaveDaysDTO.getLeaveCount();
            if(currentBalance < 0){
                throw new IllegalArgumentException("Annual Leave Count Exceeded!");
            }
            userLeaveCountService.updateLeaveCountBalance(userId, currentBalance);

            String path = leaveApplication.getFilePath();
            if(leaveApplicationDTO.getIsFileUpdated()){
                path = fileService.fileUpdate(leaveApplicationDTO, leaveApplication.getFilePath(), userId);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            leaveApplication.setFromDate(LocalDateTime.parse(leaveApplicationDTO.getFromDate(), formatter));
            leaveApplication.setToDate(LocalDateTime.parse(leaveApplicationDTO.getToDate(), formatter));
            leaveApplication.setLeaveReason(leaveApplicationDTO.getLeaveReason());
            leaveApplication.setApplicationStatus(ApplicationStatus.Pending);
            leaveApplication.setLeaveType(leaveApplicationDTO.getLeaveType());
            leaveApplication.setEmergencyContact(leaveApplicationDTO.getEmergencyContact());
            leaveApplication.setUserId(userId);
            leaveApplication.setFilePath(path);
            //int leaveCount = getLeaveCount(leaveApplication.getFromDate().toLocalDate(), leaveApplication.getToDate().toLocalDate(), userId, leaveApplication.getId());
            leaveApplication.setLeaveCount(leaveDaysDTO.getLeaveCount());

            userLeaveDaysService.deleteByApplicationId(leaveApplication.getId());
            userLeaveDaysService.saveAllByDateList(leaveDaysDTO.getLeaveDays(), leaveApplication.getId());
        }
        return "Successfully updated";
    }

    @Transactional
    public String saveLeaveApplication(LeaveApplicationDTO leaveApplicationDTO, Long userId) throws IllegalArgumentException, FileSaveException{

        LeaveApplication leaveApplication = convertDtoToEntity(leaveApplicationDTO);
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

        return "Successfully saved";
    }

    public LeaveDaysDTO getLeaveCount(LocalDate fromDate, LocalDate toDate, Long userId, Long id){

        List<LocalDate> localDateList = new ArrayList<>();
        List<LocalDate> blockedDates = getAllLeaveDates(userId, id);
        Set<Long> blockedDateSet = new HashSet<>();
        for(LocalDate date: blockedDates){
            blockedDateSet.add(date.toEpochDay());
        }

        LocalDate weekday = fromDate;
        int leaveDays = 1;
        while (weekday.isBefore(toDate)) {
            if(!blockedDateSet.contains(weekday.toEpochDay())){
                leaveDays++;
                localDateList.add(weekday);
            }
            if (weekday.getDayOfWeek() == DayOfWeek.FRIDAY)
                weekday = weekday.plusDays(3);
            else
                weekday = weekday.plusDays(1);
        }
        localDateList.add(toDate);
        log.info("total leave count:"+ leaveDays);

        LeaveDaysDTO leaveDaysDTO = LeaveDaysDTO.builder()
                .leaveDays(localDateList)
                .leaveCount(leaveDays)
                .build();
        return leaveDaysDTO;
    }

    @Transactional
    public void updateRequestStatus(Long id, ApplicationStatus status){
        Optional<LeaveApplication> leaveApplicationOptional = leaveRepository.findById(id);
        if(leaveApplicationOptional.isPresent()){
            LeaveApplication application = leaveApplicationOptional.get();
            application.setApplicationStatus(status);
        }
    }

    public LeaveApplication getById(Long id){
        Optional<LeaveApplication> leaveApplicationOptional = leaveRepository.findById(id);
        if(leaveApplicationOptional.isPresent()){
            LeaveApplication leaveApplication =  leaveApplicationOptional.get();

            //fetch file
            if(leaveApplication.getFilePath() != null && !leaveApplication.getFilePath().equals("")){
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
            if(users.getRole().equals(Role.Admin)){
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
        List<UserLeaveApplicationDTO> applicationList = leaveRepository.findByApplicationStatusOrderByIdDesc(ApplicationStatus.Approved);
        return applicationList;
    }

    public List<UserLeaveApplicationDTO> getApprovedListByName(String name){
        return leaveRepository.findApprovedListByName(name, ApplicationStatus.Approved);
    }

    public LeaveApplication convertDtoToEntity(LeaveApplicationDTO leaveApplicationDTO){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LeaveApplication leaveApplication = LeaveApplication.builder()
                .fromDate(LocalDateTime.parse(leaveApplicationDTO.getFromDate(), formatter))
                .toDate(LocalDateTime.parse(leaveApplicationDTO.getToDate(), formatter))
                .leaveReason(leaveApplicationDTO.getLeaveReason())
                .leaveType(leaveApplicationDTO.getLeaveType())
                .applicationStatus(leaveApplicationDTO.getApplicationStatus())
                .emergencyContact(leaveApplicationDTO.getEmergencyContact())
                .build();

        return leaveApplication;
    }

    public List<LocalDate> getAllLeaveDates(Long userId, Long id){
        List<ProjectDateRange> leaveDatesRange = leaveRepository.findByUserIdAndIdNotAndApplicationStatusIn(
                userId,
                id,
                List.of(ApplicationStatus.Pending, ApplicationStatus.Approved));

        List<LocalDate> list = new ArrayList<>();
        for(ProjectDateRange range: leaveDatesRange) {
            list.addAll(range.getFromDate().toLocalDate().datesUntil(range.getToDate().toLocalDate())
                    .collect(Collectors.toList()));
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
