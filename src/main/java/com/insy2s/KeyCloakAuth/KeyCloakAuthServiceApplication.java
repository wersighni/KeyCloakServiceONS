package com.insy2s.KeyCloakAuth;

import com.insy2s.KeyCloakAuth.model.Access;
import com.insy2s.KeyCloakAuth.model.Role;
import com.insy2s.KeyCloakAuth.model.User;
import com.insy2s.KeyCloakAuth.repository.RoleRepository;
import com.insy2s.KeyCloakAuth.service.IAccessService;
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
@Autowired
private IAccessService accessService;


	public static void main(String[] args) {
		SpringApplication.run(KeyCloakAuthServiceApplication.class, args);
	}
	@Bean
	CommandLineRunner start(){
		return args-> {

			setDefaultAccess();
			Role admin = new Role();
			admin.setName("ADMIN");
			saveRole(admin);

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

    private void setDefaultAccess(){
		//menu cours
       Access menuCours=accessService.create(new Access("Cours","Cours","Menu"));
		Access listCours=accessService.create(new Access("List des cours","lstCours","Page","cours",menuCours));
		Access addCours=accessService.create(new Access("Ajouter un cours","addCours","Page","ajoutcours",menuCours));
		//menu projet
		Access menuProjet=accessService.create(new Access("Projet","Projet","Menu"));
		Access listProjet=accessService.create(new Access("List des projets","lstProjets","Page","projets",menuProjet));
		Access addProjet=accessService.create(new Access("Ajouter un projet","createprojet","Page","createprojet",menuProjet));
		Access updateProjet=accessService.create(new Access("Modifier un projet","updateProject","Action",listProjet));


	}

	private Role saveRole(Role role)
	{
		Optional<Role> roleSearched=roleRepository.findByName(role.getName());
		if(roleSearched.isEmpty()){
			role.setAccessList(accessService.getAllAccess());
			role=roleRepository.save(role);
			System.out.println("The role with name "+role.getName() +" saved ");

		}
		else{
			System.out.println("The role with name "+role.getName() +" found ");
		}
		return role;
	}


}



