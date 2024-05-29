package org.melhem.hopital;



import java.util.Date;

import org.melhem.hopital.entity.Patient;
import org.melhem.hopital.repository.PatientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HopitalApplication implements CommandLineRunner{
	
	private PatientRepository patientRepository;
	
	public HopitalApplication(PatientRepository patientRepository) {
		super();
		this.patientRepository = patientRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(HopitalApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		patientRepository.save(new Patient(null, "p1",true, new Date() ,10));
		patientRepository.save(new Patient(null, "p2",false, new Date() ,20));
		patientRepository.save(new Patient(null, "p3",true, new Date() ,30));
		patientRepository.save(new Patient(null, "p4",false, new Date() ,40));
		patientRepository.save(new Patient(null, "p5",true, new Date() ,50));
	}

}
