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
			admin=saveRole(admin)		;

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

		Access menuAdmin=accessService.create(new Access("Administration","Admin","Menu"));
		Access listUsers=accessService.create(new Access("List des utilisateurs","users","Page","users",menuAdmin));
		Access addAdmin=accessService.create(new Access("Ajouter un role","add-role","Page","add-role",menuAdmin));
		Access listRoles=accessService.create(new Access("List des roles","roles","Page","roles",menuAdmin));
		Access updateRoles=accessService.create(new Access("Modifier un role","updateRole","Action",listRoles));
		Access addRoles=accessService.create(new Access("Ajouter un role","addRole","Action",listRoles));
		Access deleteRoles=accessService.create(new Access("supprimer un role","deleteRole","Action",listRoles));
		Access updateUsers=accessService.create(new Access("Modifier un utilisateur","updateUser","Action",listUsers));
		Access addUsers=accessService.create(new Access("Ajouter un utilisateur","addUser","Action",listUsers));
		Access deleteUsers=accessService.create(new Access("supprimer un utilisateur","deleteUser","Action",listUsers));

		Access listGlobalAccess=accessService.create(new Access("List des accés","access","Page","access",menuAdmin));

		Access updateAccess=accessService.create(new Access("Modifier un accés","updateAccess","Action",listGlobalAccess));
		Access deleteAccess=accessService.create(new Access("Supprimer un accés","deleteAccess","Action",listGlobalAccess));
		Access addAccess=accessService.create(new Access("Ajouter un accés","addAccess","Action",listGlobalAccess));



		//menu cours
       Access menuCours=accessService.create(new Access("Cours","Cours","Menu"));
		Access listCours=accessService.create(new Access("List des cours","lstCours","Page","cours",menuCours));
		Access addCours=accessService.create(new Access("Ajouter un cours","addCours","Page","ajoutcours",menuCours));
		//menu projet
		Access menuProjet=accessService.create(new Access("Projet","Projet","Menu"));
		Access listProjet=accessService.create(new Access("List des projets","lstProjets","Page","projets",menuProjet));
		Access addProjet=accessService.create(new Access("Ajouter un projet","createprojet","Page","createprojet",menuProjet));
		Access suivi=accessService.create(new Access("Suivre un projet","suivi","Page","suivi",menuProjet));
		Access updateProjet=accessService.create(new Access("Modifier un projet","updateProjet","Action",listProjet));

		//membre
		Access menuMembre=accessService.create(new Access("Membre","Membre","Menu"));
		Access listMembre=accessService.create(new Access("List des Membres","membres","Page","membres",menuMembre));
		Access addMembre=accessService.create(new Access("Ajouter un Membre","ajout-membre","Page","ajout-membre",menuMembre));
		Access updateMembre=accessService.create(new Access("Modifier un Membre","updateMember","Action",listMembre));
		Access listpos=accessService.create(new Access("List des positions","list-position","Page","list-position",menuMembre));

		 //Eval
		Access menueval=accessService.create(new Access("Evaluation","Evaluation","Menu"));
		Access listeval=accessService.create(new Access("Evaluation","eval","Page","eval",menueval));
		Access addeval=accessService.create(new Access("Passer examin","passer_examen","Page","passer_examen",menueval));
		Access quizz=accessService.create(new Access("Quiz","quizz","Page","quizz",menueval));
		Access tuteur=accessService.create(new Access("Aspace tuteur","tuteur","Page","tuteur",menueval));
//

//assistance

		Access menuAssistance=accessService.create(new Access("Assistance","Assistance","Menu"));
		Access listAssistance=accessService.create(new Access("List des Assistances","list-Assistance","Page","list-Assistance",menuAssistance));
		Access addAssistance=accessService.create(new Access("Demande d'aide","demandeAide","Page","demandeAide",menuAssistance));

		Access menuParam=accessService.create(new Access("Paramétres","Param","Menu"));
		Access listParam=accessService.create(new Access("List des compétences","skills","Page","skills",menuParam));


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



