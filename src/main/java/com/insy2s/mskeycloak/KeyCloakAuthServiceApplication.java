package com.insy2s.mskeycloak;

import com.insy2s.mskeycloak.dto.AccessDto;
import com.insy2s.mskeycloak.dto.CreateAccess;
import com.insy2s.mskeycloak.dto.mapper.IAccessMapper;
import com.insy2s.mskeycloak.model.Access;
import com.insy2s.mskeycloak.model.Role;
import com.insy2s.mskeycloak.repository.IRoleRepository;
import com.insy2s.mskeycloak.repository.IUserRepository;
import com.insy2s.mskeycloak.service.IAccessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@EnableDiscoveryClient
@SpringBootApplication
@RequiredArgsConstructor
@EnableFeignClients
public class KeyCloakAuthServiceApplication {

    private final IRoleRepository roleRepository;
    private final IAccessService accessService;
    private final IUserRepository userRepository;
    private final IAccessMapper accessMapper;

    public static void main(String[] args) {
        SpringApplication.run(KeyCloakAuthServiceApplication.class, args);
    }

    //TODO: need to use database management tool to add data to the database, like liquibase
    @Bean
    CommandLineRunner start() {
        return args -> {
            setDefaultAccess();
            Role admin = new Role();
            admin.setName("ADMIN");
            admin.setDescription("Administrateur");
            saveRole(admin);
            Role tuteurProfessionnel = new Role();
            tuteurProfessionnel.setName("Tuteur Professionnel");
            tuteurProfessionnel.setDescription("Tuteur Professionnel");
            saveRole(tuteurProfessionnel);
            Role apprenant = new Role();
            apprenant.setName("Apprenant");
            apprenant.setDescription("Apprenant");
            saveRole(apprenant);
            Role tuteurAcademique = new Role();
            tuteurAcademique.setName("Tuteur Academique");
            tuteurAcademique.setDescription("Tuteur Academique");
            saveRole(tuteurAcademique);
            Role apprenantAide = new Role();
            apprenantAide.setName("Apprenant d'Aide");
            apprenantAide.setDescription("Apprenant d'Aide");
            saveRole(apprenantAide);
            Role apprenantVerification = new Role();
            apprenantVerification.setName("Apprenant de Verif");
            apprenantVerification.setDescription("Apprenant de Verif");
            saveRole(apprenantVerification);
        };
    }

    private void setDefaultAccess() {
        AccessDto menuAdmin = accessService.create(new CreateAccess("Administration", "Admin", "Menu", null, null, new ArrayList<>()));
        AccessDto listUsers = accessService.create(new CreateAccess("Liste des utilisateurs", "users", "Page", "users", accessMapper.toEntity(menuAdmin), new ArrayList<>()));
        accessService.create(new CreateAccess("Ajouter un role", "add-role", "Page", "add-role", accessMapper.toEntity(menuAdmin), new ArrayList<>()));
        AccessDto listRoles = accessService.create(new CreateAccess("Liste des roles", "roles", "Page", "roles", accessMapper.toEntity(menuAdmin), new ArrayList<>()));
        accessService.create(new CreateAccess("Modifier un role", "updateRole", "Action", null, accessMapper.toEntity(listRoles), new ArrayList<>()));
        accessService.create(new CreateAccess("Ajouter un role", "addRole", "Action", null, accessMapper.toEntity(listRoles), new ArrayList<>()));
        accessService.create(new CreateAccess("supprimer un role", "deleteRole", "Action", null, accessMapper.toEntity(listRoles), new ArrayList<>()));
        accessService.create(new CreateAccess("Modifier un utilisateur", "updateUser", "Action", null, accessMapper.toEntity(listUsers), new ArrayList<>()));
        accessService.create( new CreateAccess("Ajouter un utilisateur", "addUser", "Action", null, accessMapper.toEntity(listUsers), new ArrayList<>()));
        accessService.create(new CreateAccess("supprimer un utilisateur", "deleteUser", "Action", null, accessMapper.toEntity(listUsers), new ArrayList<>()));
        AccessDto listGlobalAccess = accessService.create( new CreateAccess("List des accés", "access", "Page", "access", accessMapper.toEntity(menuAdmin), new ArrayList<>()));
        accessService.create(new CreateAccess("Modifier un accés", "updateAccess", "Action", null, accessMapper.toEntity(listGlobalAccess), new ArrayList<>()));
        accessService.create(new CreateAccess("Supprimer un accés", "deleteAccess", "Action", null, accessMapper.toEntity(listGlobalAccess), new ArrayList<>()));
        accessService.create(new CreateAccess("Ajouter un accés", "addAccess", "Action", null, accessMapper.toEntity(listGlobalAccess), new ArrayList<>()));

        //menu cours
        AccessDto menuCours = accessService.create(new CreateAccess("Cours", "Cours", "Menu", null, null, new ArrayList<>()));
        AccessDto listCours = accessService.create(new CreateAccess("Liste des cours", "lstCours", "Page", "cours", accessMapper.toEntity(menuCours), new ArrayList<>()));
        accessService.create(new CreateAccess("Ajouter un cours", "addCours", "Page", "ajoutcours", accessMapper.toEntity(menuCours), new ArrayList<>()));
        accessService.create(new CreateAccess("Modifier un Cours", "updateCourses", "Action", null, accessMapper.toEntity(listCours), new ArrayList<>()));
        accessService.create(new CreateAccess("Ajouter un Cours", "addCourses", "Action", null, accessMapper.toEntity(listCours), new ArrayList<>()));
        accessService.create(new CreateAccess("supprimer un Cours", "deleteCourses", "Action", null, accessMapper.toEntity(listCours), new ArrayList<>()));
        accessService.create(new CreateAccess("Ajouter une session", "addSession", "Page", "ajoutSession", accessMapper.toEntity(menuCours), new ArrayList<>()));
        accessService.create(new CreateAccess("Ajouter un programme", "addProgram", "Page", "ajoutProgram", accessMapper.toEntity(menuCours), new ArrayList<>()));
        accessService.create(new CreateAccess("Liste des programmes", "ListProgram", "Page", "listProgram", accessMapper.toEntity(menuCours), new ArrayList<>()));
        accessService.create(new CreateAccess("Liste des sessions", "ListSession", "Page", "listSession", accessMapper.toEntity(menuCours), new ArrayList<>()));
        accessService.create(new CreateAccess("Type de phase", "typePhase", "Page", "typePhase", accessMapper.toEntity(menuCours), new ArrayList<>()));

        //menu projet
        AccessDto menuProjet = accessService.create(new CreateAccess("Projet", "Projet", "Menu", null, null, new ArrayList<>()));
        AccessDto listProjet = accessService.create(new CreateAccess("List des projets", "lstProjets", "Page", "projets", accessMapper.toEntity(menuProjet), new ArrayList<>()));
        accessService.create(new CreateAccess("Ajouter un projet", "createprojet", "Page", "createprojet", accessMapper.toEntity(menuProjet), new ArrayList<>()));
        accessService.create(new CreateAccess("Suivre un projet", "suivi", "Page", "suivi", accessMapper.toEntity(menuProjet), new ArrayList<>()));
        accessService.create(new CreateAccess("Modifier un Projet", "updateProject", "Action", null, accessMapper.toEntity(listProjet), new ArrayList<>()));
        accessService.create(new CreateAccess("Ajouter un Projet", "addProject", "Action", null, accessMapper.toEntity(listProjet), new ArrayList<>()));
        accessService.create(new CreateAccess("supprimer un Projet", "deleteProject", "Action", null, accessMapper.toEntity(listProjet), new ArrayList<>()));

        //membre
        AccessDto menuMembre = accessService.create(new CreateAccess("Membre", "Membre", "Menu", null, null, new ArrayList<>()));
        AccessDto listMembre = accessService.create(new CreateAccess("List des Membres", "membres", "Page", "membres", accessMapper.toEntity(menuMembre), new ArrayList<>()));
        accessService.create(new CreateAccess("Ajouter un Membre", "ajout-membre", "Page", "ajout-membre", accessMapper.toEntity(menuMembre), new ArrayList<>()));
        accessService.create(new CreateAccess("Modifier un Membre", "updateMember", "Action", null, accessMapper.toEntity(listMembre), new ArrayList<>()));
        accessService.create(new CreateAccess("List des positions", "list-position", "Page", "list-position", accessMapper.toEntity(menuMembre), new ArrayList<>()));

        //Eval
        AccessDto menueval = accessService.create(new CreateAccess("Evaluation", "Evaluation", "Menu", null, null, new ArrayList<>()));
        accessService.create(new CreateAccess("Evaluation", "eval", "Page", "eval", accessMapper.toEntity(menueval), new ArrayList<>()));
        accessService.create(new CreateAccess("Passer examin", "passer_examen", "Page", "passer_examen", accessMapper.toEntity(menueval), new ArrayList<>()));
        accessService.create(new CreateAccess("Quiz", "quizz", "Page", "quizz", accessMapper.toEntity(menueval), new ArrayList<>()));
        accessService.create(new CreateAccess("Aspace tuteur", "tuteur", "Page", "tuteur", accessMapper.toEntity(menueval), new ArrayList<>()));

        //assistance
        AccessDto menuAssistance = accessService.create(new CreateAccess("Assistance", "Assistance", "Menu", null, null, new ArrayList<>()));
        accessService.create(new CreateAccess("List des Assistances", "list-Assistance", "Page", "list-Assistance", accessMapper.toEntity(menuAssistance), new ArrayList<>()));
        accessService.create(new CreateAccess("Demande d'aide", "demandeAide", "Page", "demandeAide", accessMapper.toEntity(menuAssistance), new ArrayList<>()));
       //paramétre
        AccessDto menuParam = accessService.create(new CreateAccess("Paramétres", "Param", "Menu", null, null, new ArrayList<>()));
        accessService.create(new CreateAccess("List des compétences", "skills", "Page", "skills", accessMapper.toEntity(menuParam), new ArrayList<>()));
        accessService.create(new CreateAccess("List des thèmes","themeList","Page","themeList", accessMapper.toEntity(menuParam), new ArrayList<>()));
        //notification
        AccessDto menuNotification = accessService.create(new CreateAccess("Notification", "Notification", "Menu", null, null, new ArrayList<>()));
        accessService.create(new CreateAccess("List des type de notification", "typelist", "Page", "typelist", accessMapper.toEntity(menuNotification), new ArrayList<>()));
        accessService.create(new CreateAccess("List des Notifications", "notificationss", "Page", "notificationss", accessMapper.toEntity(menuNotification), new ArrayList<>()));
        accessService.create(new CreateAccess("Ajpouter une notification", "addnotification", "Page", "addnotification", accessMapper.toEntity(menuNotification), new ArrayList<>()));


//Forum
        AccessDto   menuForum=accessService.create(new CreateAccess("Forum","Forum","Menu",null, null, new ArrayList<>()));
        accessService.create(new CreateAccess("Forum","forum-main","Page","forum-main", accessMapper.toEntity(menuForum), new ArrayList<>()));


    }

    private void saveRole(Role role) {
        Optional<Role> roleSearched = roleRepository.findByName(role.getName());
        if (roleSearched.isEmpty()) {
            role.setAccessList(accessService.findAllWithoutChildren());
            role = roleRepository.save(role);
            log.info("The role with name '{}' SAVED.", role.getName());
        } else {
            log.info("The role with name '{}' FOUND.", role.getName());
        }
    }

}