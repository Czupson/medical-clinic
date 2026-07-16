package com.Czupson.medical_clinic.mapper;

import com.Czupson.medical_clinic.dto.CreatePatientCommand;
import com.Czupson.medical_clinic.dto.UpdatePatientCommand;
import com.Czupson.medical_clinic.model.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public Patient toPatient(CreatePatientCommand command){
        return new Patient(
                null,
                command.getEmail(),
                command.getPassword(),
                command.getIdCardNumber(),
                command.getFirstName(),
                command.getLastName(),
                command.getPhoneNumber(),
                command.getBirthday()
        );
    }

    public Patient toPatient(UpdatePatientCommand command, Long id){
        return new Patient(
                id,
                command.getEmail(),
                command.getPassword(),
                command.getIdCardNumber(),
                command.getFirstName(),
                command.getLastName(),
                command.getPhoneNumber(),
                command.getBirthday()

        );
    }
}
