package com.enosis.leavemanagement.service;

import com.enosis.leavemanagement.dto.LeaveApplicationDTO;
import com.enosis.leavemanagement.enums.ApplicationStatus;
import com.enosis.leavemanagement.enums.Role;
import com.enosis.leavemanagement.exceptions.FileSaveException;
import com.enosis.leavemanagement.model.LeaveApplication;
import com.enosis.leavemanagement.model.Users;
import com.enosis.leavemanagement.repository.LeaveRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LeaveService {
    private final LeaveRepository leaveRepository;
    private final FileService fileService;
    private final UserService userService;

    public String saveLeaveApplication(LeaveApplicationDTO leaveApplicationDTO){
        String path = "";
        try {
            if(leaveApplicationDTO.getFile() != null) {
                path = fileService.saveFile(leaveApplicationDTO.getFile());
            }
        } catch (FileSaveException e) {
            e.printStackTrace();
            return e.getMessage();
        }catch (RuntimeException r){
            r.printStackTrace();
            return r.getMessage();
        }catch(Exception e){
            e.printStackTrace();
            return e.getMessage();
        }

        LeaveApplication leaveApplication = convertDtoToEntity(leaveApplicationDTO);
        leaveApplication.setFilePath(path);
        leaveApplication.setApplicationStatus(ApplicationStatus.Pending);
        leaveApplication.setUserId(1L);
        save(leaveApplication);

        //leave count
        long leaveCount = ChronoUnit.DAYS.between(leaveApplication.getFromDate(), leaveApplication.getToDate());
        System.out.println("leave count: "+ leaveCount);
        return "Successfully saved";
    }
    @Transactional
    public void updateRequestStatus(Long id, ApplicationStatus status){
        Optional<LeaveApplication> leaveApplicationOptional = leaveRepository.findById(id);
        if(leaveApplicationOptional.isPresent()){
            LeaveApplication application = leaveApplicationOptional.get();
            application.setApplicationStatus(status);
        }
    }

    public List<LeaveApplication> getAllPendingLeaveRequests(String userEmail){
        Optional<Users> usersOptional = userService.findByEmail(userEmail);
        List<LeaveApplication> applicationList;
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

    public List<LeaveApplication> getAllApprovedLeaveRequests(){
        List<LeaveApplication> applicationList = leaveRepository.findByApplicationStatusOrderByIdDesc(ApplicationStatus.Approved);
        return applicationList;
    }

    public void save(LeaveApplication leaveApplication){
        leaveRepository.save(leaveApplication);
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
}
