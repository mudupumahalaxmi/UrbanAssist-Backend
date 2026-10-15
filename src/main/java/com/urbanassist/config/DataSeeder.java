package com.urbanassist.config;

import com.urbanassist.entity.*;
import com.urbanassist.repository.ComplaintRepository;
import com.urbanassist.repository.DepartmentRepository;
import com.urbanassist.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

/**
 * DataSeeder — Safe, idempotent initial data seeder.
 * Populates standard municipal departments, default users, and sample complaints on startup if not present.
 */
@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner initDatabase(
            DepartmentRepository departmentRepository,
            UserRepository userRepository,
            ComplaintRepository complaintRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Seed Departments safely
            seedDepartment(departmentRepository, "Roads & Traffic Infrastructure", "ROADS",
                    "Potholes, broken roads, damaged street signs, traffic signals", "roads@urbanassist.gov");

            seedDepartment(departmentRepository, "Solid Waste Management & Sanitation", "SANITATION",
                    "Garbage accumulation, uncleaned streets, illegal dumping", "sanitation@urbanassist.gov");

            seedDepartment(departmentRepository, "Water Supply & Drainage", "WATER",
                    "Water leaks, pipeline bursts, sewage overflow, low pressure", "water@urbanassist.gov");

            seedDepartment(departmentRepository, "Electricity & Street Lighting", "ELECTRICITY",
                    "Broken streetlights, dangling wires, transformer faults", "electricity@urbanassist.gov");

            seedDepartment(departmentRepository, "Parks & Public Amenities", "PARKS",
                    "Fallen trees, damaged park equipment, public property vandalism", "parks@urbanassist.gov");

            seedDepartment(departmentRepository, "Public Health & Hygiene", "PUBLIC_HEALTH",
                    "Mosquito breeding, stray animal hazard, food safety concern", "health@urbanassist.gov");

            // 2. Seed Default Admin safely
            if (!userRepository.existsByEmail("admin@urbanassist.gov")) {
                User admin = new User(
                        "System Administrator",
                        "admin@urbanassist.gov",
                        passwordEncoder.encode("Admin@123456"),
                        Role.ROLE_ADMIN
                );
                userRepository.save(admin);
            }

            // 3. Seed Default Staff member safely
            if (!userRepository.existsByEmail("staff@urbanassist.gov")) {
                Department roadsDept = departmentRepository.findByCode("ROADS").orElse(null);
                User staff = new User(
                        "Municipal Field Officer",
                        "staff@urbanassist.gov",
                        passwordEncoder.encode("Staff@123456"),
                        Role.ROLE_STAFF
                );
                staff.setDepartment(roadsDept);
                staff.setPhone("040-21111111");
                userRepository.save(staff);
            }

            // 4. Seed Default Citizen safely (for testing and public verification)
            User citizen = userRepository.findByEmail("citizen@urbanassist.gov").orElse(null);
            if (citizen == null) {
                citizen = new User(
                        "Citizen User",
                        "citizen@urbanassist.gov",
                        passwordEncoder.encode("Citizen@123456"),
                        Role.ROLE_CITIZEN
                );
                citizen.setPhone("040-33333333");
                citizen = userRepository.save(citizen);
            }

            // 5. Seed Initial Sample Complaints if none exist (for local development)
            if (complaintRepository.count() == 0) {
                Department roadsDept = departmentRepository.findByCode("ROADS").orElse(null);
                Department waterDept = departmentRepository.findByCode("WATER").orElse(null);
                Department elecDept = departmentRepository.findByCode("ELECTRICITY").orElse(null);

                // Complaint 1: Roads (RESOLVED, Dept 1)
                Complaint c1 = new Complaint();
                c1.setComplaintNumber("URA-578156");
                c1.setCitizen(citizen);
                c1.setDepartment(roadsDept);
                c1.setTitle("Pothole on the road");
                c1.setDescription("Large pothole near main junction causing traffic slowdowns.");
                c1.setCategory(ComplaintCategory.ROADS);
                c1.setSeverityLevel(SeverityLevel.MEDIUM);
                c1.setPriorityScore(BigDecimal.valueOf(30));
                c1.setStatus(ComplaintStatus.RESOLVED);
                c1.setLatitude(new BigDecimal("17.38500000"));
                c1.setLongitude(new BigDecimal("78.48670000"));
                c1.setAddress("Banjara Hills Road No 1");
                c1.setPincode("500001");
                complaintRepository.save(c1);

                // Complaint 2: Water (PENDING, Dept 3)
                Complaint c2 = new Complaint();
                c2.setComplaintNumber("URA-357088");
                c2.setCitizen(citizen);
                c2.setDepartment(waterDept);
                c2.setTitle("Drainage overflow and water leakage");
                c2.setDescription("Severe drainage blockage causing wastewater accumulation on street.");
                c2.setCategory(ComplaintCategory.WATER);
                c2.setSeverityLevel(SeverityLevel.MEDIUM);
                c2.setPriorityScore(BigDecimal.valueOf(30));
                c2.setStatus(ComplaintStatus.PENDING);
                c2.setLatitude(new BigDecimal("17.38500000"));
                c2.setLongitude(new BigDecimal("78.48670000"));
                c2.setAddress("Anurag University Gate");
                c2.setLandmark("Near PG Hostel");
                c2.setPincode("500001");
                complaintRepository.save(c2);

                // Complaint 3: Electricity (IN_PROGRESS, Dept 4)
                Complaint c3 = new Complaint();
                c3.setComplaintNumber("URA-125119");
                c3.setCitizen(citizen);
                c3.setDepartment(elecDept);
                c3.setTitle("Flickering street light on Main St");
                c3.setDescription("Street light pole #45 sparking and flickering since yesterday.");
                c3.setCategory(ComplaintCategory.ELECTRICITY);
                c3.setSeverityLevel(SeverityLevel.CRITICAL);
                c3.setPriorityScore(BigDecimal.valueOf(65));
                c3.setStatus(ComplaintStatus.IN_PROGRESS);
                c3.setLatitude(new BigDecimal("17.38500000"));
                c3.setLongitude(new BigDecimal("78.48670000"));
                c3.setAddress("Main Street Sector 4");
                c3.setPincode("500001");
                complaintRepository.save(c3);

                // Complaint 4: Roads (IN_PROGRESS, Dept 1) — active complaint for field triage
                Complaint c4 = new Complaint();
                c4.setComplaintNumber("URA-841920");
                c4.setCitizen(citizen);
                c4.setDepartment(roadsDept);
                c4.setTitle("Broken median divider on Road No 36");
                c4.setDescription("Damaged concrete divider posing hazard to vehicular traffic.");
                c4.setCategory(ComplaintCategory.ROADS);
                c4.setSeverityLevel(SeverityLevel.HIGH);
                c4.setPriorityScore(BigDecimal.valueOf(55));
                c4.setStatus(ComplaintStatus.IN_PROGRESS);
                c4.setLatitude(new BigDecimal("17.43200000"));
                c4.setLongitude(new BigDecimal("78.40700000"));
                c4.setAddress("Road No 36, Jubilee Hills Checkpost");
                c4.setPincode("500033");
                complaintRepository.save(c4);
            }
        };
    }

    private void seedDepartment(DepartmentRepository repo, String name, String code, String description, String email) {
        if (!repo.existsByCode(code)) {
            repo.save(new Department(name, code, description, email));
        }
    }
}
