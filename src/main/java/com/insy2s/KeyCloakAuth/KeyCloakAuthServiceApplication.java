package com.insy2s.keycloakauth;

import com.insy2s.keycloakauth.model.Access;
import com.insy2s.keycloakauth.model.Role;
import com.insy2s.keycloakauth.repository.RoleRepository;
import com.insy2s.keycloakauth.service.IAccessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@Slf4j
@EnableDiscoveryClient
@SpringBootApplication
@RequiredArgsConstructor
public class KeyCloakAuthServiceApplication {

    private final RoleRepository roleRepository;
    private final IAccessService accessService;

    public static void main(String[] args) {
        SpringApplication.run(KeyCloakAuthServiceApplication.class, args);
    }

    //TODO: need to use database management tool to add data to the database, like liquibase
    @Bean
    CommandLineRunner start(){
        return args-> {
            setDefaultAccess();
            Role admin = new Role();
            admin.setName("ADMIN");
            admin=saveRole(admin);
            saveRole(admin);
            Role tuteurProfessionnel = new Role();
            tuteurProfessionnel.setName("Tuteur Professionnel");
            saveRole(tuteurProfessionnel);
            Role apprenant = new Role();
            apprenant.setName("Apprenant");
            saveRole(apprenant);
            Role tuteurAcademique = new Role();
            tuteurAcademique.setName("Tuteur Academique");
            saveRole(tuteurAcademique);
            Role apprenantAide = new Role();
            apprenantAide.setName("Apprenant d'Aide");
            saveRole(apprenantAide)		;
            Role apprenantVerification = new Role();
            apprenantVerification.setName("Apprenant de Verif");
            saveRole(apprenantVerification)	;
        };
    }

    private void setDefaultAccess(){
        Access menuAdmin = accessService.create(new Access("Administration","Admin","Menu"));
        Access listUsers = accessService.create(new Access("Liste des utilisateurs","users","Page","users",menuAdmin));
        accessService.create(new Access("Ajouter un role","add-role","Page","add-role",menuAdmin));
        Access listRoles = accessService.create(new Access("Liste des roles","roles","Page","roles",menuAdmin));
        accessService.create(new Access("Modifier un role","updateRole","Action",listRoles));
        accessService.create(new Access("Ajouter un role","addRole","Action",listRoles));
        accessService.create(new Access("supprimer un role","deleteRole","Action",listRoles));
        accessService.create(new Access("Modifier un utilisateur","updateUser","Action",listUsers));
        accessService.create(new Access("Ajouter un utilisateur","addUser","Action",listUsers));
        accessService.create(new Access("supprimer un utilisateur","deleteUser","Action",listUsers));

        Access listGlobalAccess = accessService.create(new Access("List des accés","access","Page","access",menuAdmin));
        accessService.create(new Access("Modifier un accés","updateAccess","Action",listGlobalAccess));
        accessService.create(new Access("Supprimer un accés","deleteAccess","Action",listGlobalAccess));
        accessService.create(new Access("Ajouter un accés","addAccess","Action",listGlobalAccess));

        //menu cours
        Access menuCours = accessService.create(new Access("Cours","Cours","Menu"));
        Access listCours = accessService.create(new Access("Liste des cours","lstCours","Page","cours",menuCours));
        accessService.create(new Access("Ajouter un cours","addCours","Page","ajoutcours",menuCours));
        accessService.create(new Access("Modifier un Cours","updateCourses","Action",listCours));
        accessService.create(new Access("Ajouter un Cours","addCourses","Action",listCours));
        accessService.create(new Access("supprimer un Cours","deleteCourses","Action",listCours));
        accessService.create(new Access("Ajouter une session","addSession","Page","ajoutSession",menuCours));
        accessService.create(new Access("Ajouter un programme","addProgram","Page","ajoutProgram",menuCours));
        accessService.create(new Access("Liste des programmes","ListProgram","Page","listProgram",menuCours));
        accessService.create(new Access("Liste des sessions","ListSession","Page","listSession",menuCours));
        accessService.create(new Access("Type de phase","typePhase","Page","typePhase",menuCours));

        //menu projet
        Access menuProjet = accessService.create(new Access("Projet","Projet","Menu"));
        Access listProjet = accessService.create(new Access("List des projets","lstProjets","Page","projets",menuProjet));
        accessService.create(new Access("Ajouter un projet","createprojet","Page","createprojet",menuProjet));
        accessService.create(new Access("Suivre un projet","suivi","Page","suivi",menuProjet));
        accessService.create(new Access("Modifier un Projet","updateProject","Action",listProjet));
        accessService.create(new Access("Ajouter un Projet","addProject","Action",listProjet));
        accessService.create(new Access("supprimer un Projet","deleteProject","Action",listProjet));

        //membre
        Access menuMembre = accessService.create(new Access("Membre","Membre","Menu"));
        Access listMembre = accessService.create(new Access("List des Membres","membres","Page","membres",menuMembre));
        accessService.create(new Access("Ajouter un Membre","ajout-membre","Page","ajout-membre",menuMembre));
        accessService.create(new Access("Modifier un Membre","updateMember","Action",listMembre));
        accessService.create(new Access("List des positions","list-position","Page","list-position",menuMembre));

        //Eval
        Access menueval = accessService.create(new Access("Evaluation","Evaluation","Menu"));
        accessService.create(new Access("Evaluation","eval","Page","eval",menueval));
        accessService.create(new Access("Passer examin","passer_examen","Page","passer_examen",menueval));
        accessService.create(new Access("Quiz","quizz","Page","quizz",menueval));
        accessService.create(new Access("Aspace tuteur","tuteur","Page","tuteur",menueval));

        //assistance
        Access menuAssistance = accessService.create(new Access("Assistance","Assistance","Menu"));
        accessService.create(new Access("List des Assistances","list-Assistance","Page","list-Assistance",menuAssistance));
        accessService.create(new Access("Demande d'aide","demandeAide","Page","demandeAide",menuAssistance));
        Access menuParam = accessService.create(new Access("Paramétres","Param","Menu"));
        accessService.create(new Access("List des compétences","skills","Page","skills",menuParam));

        //notification
        Access menuNotification = accessService.create(new Access("Notification","Notification","Menu"));
        accessService.create(new Access("List des type de notification","typelist","Page","typelist",menuNotification));
        accessService.create(new Access("List des Notifications","notificationss","Page","notificationss",menuNotification));
        accessService.create(new Access("Ajpouter une notification","addnotification","Page","addnotification",menuNotification));
    }

    private Role saveRole(Role role) {
        Optional<Role> roleSearched = roleRepository.findByName(role.getName());
        if (roleSearched.isEmpty()) {
            role.setAccessList(accessService.getAllAccess());
            role = roleRepository.save(role);
            log.info("The role with name '{}' SAVED.", role.getName());
        } else {
            log.info("The role with name '{}' FOUND.", role.getName());
        }
        return role;
    }

}
