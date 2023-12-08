package com.insy2s.KeyCloakAuth;

import com.insy2s.KeyCloakAuth.model.Role;
import com.insy2s.KeyCloakAuth.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.Optional;
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class KeyCloakAuthServiceApplication {
@Autowired
private RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(KeyCloakAuthServiceApplication.class, args);
	}
	@Bean
	CommandLineRunner start(){
		return args-> {
			Role admin = new Role();
			admin.setName("ADMIN");
			saveRole(admin)		;
			Role TuteurProfessionnel = new Role();
			TuteurProfessionnel.setName("Tuteur Professionnel");
			saveRole(TuteurProfessionnel);
			Role apprenant = new Role();
			apprenant.setName("Apprenant");
			saveRole(apprenant);
			Role TuteurAcademique = new Role();
			TuteurAcademique.setName("Tuteur Academique");
			saveRole(TuteurAcademique);
			Role apprenantAide = new Role();
			apprenantAide.setName("Apprenant d'Aide");
			saveRole(apprenantAide)		;
			Role apprenantVerification = new Role();
			apprenantVerification.setName("Apprenant de Verif");
			saveRole(apprenantVerification)	;

		};}



	private void saveRole(Role role)
	{
		Optional<Role> roleSearched=roleRepository.findByName(role.getName());
		if(roleSearched.isEmpty()){
			roleRepository.save(role);
			System.out.println("The role with name "+role.getName() +" saved ");

		}
		else{
			System.out.println("The role with name "+role.getName() +" found ");
		}
	}


}



