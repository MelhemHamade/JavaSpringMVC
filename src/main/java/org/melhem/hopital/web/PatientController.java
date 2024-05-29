package org.melhem.hopital.web;

import java.util.Optional;

import org.melhem.hopital.entity.Patient;
import org.melhem.hopital.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
public class PatientController {
   
	private static final Logger logger = LoggerFactory.getLogger(PatientController.class);
	
	@Autowired
    private PatientRepository patientRepository;

    @GetMapping("/index")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "0") int p,
                        @RequestParam(name = "size", defaultValue = "4") int s,
                        @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        Page<Patient> pagePatients = patientRepository.findByNomContainingIgnoreCase(keyword, PageRequest.of(p, s));
        
        model.addAttribute("listPatients", pagePatients.getContent());
        model.addAttribute("pages", new int[pagePatients.getTotalPages()]);
        model.addAttribute("currentPage", p);
        model.addAttribute("size", s);
        model.addAttribute("keyword", keyword);

        return "patients";
    }
    
    @GetMapping("/delete/{id}")
    @Transactional
    public String deletePatient(@PathVariable("id") Long id,
                                @RequestParam(name = "page", defaultValue = "0") int page,
                                @RequestParam(name = "size", defaultValue = "4") int size,
                                @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                RedirectAttributes redirectAttributes) {
        try {
            if (patientRepository.existsById(id)) {
                patientRepository.deleteById(id);
                if (!patientRepository.existsById(id)) {
                    redirectAttributes.addFlashAttribute("successMessage", "Patient deleted successfully");
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete patient");
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Patient not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while deleting the patient");
        }
        return "redirect:/index?page=" + page + "&size=" + size + "&keyword=" + keyword;
    }
    
    @GetMapping("/formPatient")
    public String showPatientForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "formPatient";
    }

    @PostMapping("/formPatient")
    public String submitPatientForm(@Valid @ModelAttribute("patient") Patient patient, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
        	System.out.println(result.getAllErrors());
            return "formPatient";
        }
        patientRepository.save(patient);
        redirectAttributes.addFlashAttribute("successMessage", "Patient added successfully");
        return "redirect:/index";
    }
    
    @GetMapping("/edit/{id}")
    public String showEditPatientForm(@PathVariable("id") Long id, Model model, @RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("keyword") String keyword) {
        Optional<Patient> patientOpt = patientRepository.findById(id);
        if (patientOpt.isPresent()) {
            Patient patient = patientOpt.get();
            model.addAttribute("patient", patient);
            model.addAttribute("currentPage", page);
            model.addAttribute("size", size);
            model.addAttribute("keyword", keyword);
            return "formPatient";
        } else {
            return "redirect:/patients";
        }
    }

    @PostMapping("/edit/{id}")
    public String updatePatient(@PathVariable("id") Long id, @Valid @ModelAttribute("patient") Patient patient, BindingResult result, RedirectAttributes redirectAttributes, @RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("keyword") String keyword) {
        if (result.hasErrors()) {
            logger.warn("Validation errors: " + result.getAllErrors());
            return "formPatient";
        }
        logger.info("Updating patient with ID: " + id);
        Optional<Patient> patientOpt = patientRepository.findById(id);
        if (patientOpt.isPresent()) {
            Patient actualPatient = patientOpt.get();
            logger.info("Before update: " + actualPatient.toString());
            actualPatient.setNom(patient.getNom());
            actualPatient.setDateNaissance(patient.getDateNaissance());
            actualPatient.setMalade(patient.isMalade());
            actualPatient.setScore(patient.getScore());
            patientRepository.save(actualPatient);
            logger.info("After update: " + actualPatient.toString());
        } else {
            logger.warn("Patient with ID " + id + " not found during update");
            redirectAttributes.addFlashAttribute("errorMessage", "Patient not found");
            return "redirect:/index?page=" + page + "&size=" + size + "&keyword=" + keyword;
        }
        redirectAttributes.addFlashAttribute("successMessage", "Patient updated successfully");
        return "redirect:/index?page=" + page + "&size=" + size + "&keyword=" + keyword;
    }


}
