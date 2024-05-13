package com.insy2s.mskeycloak;

import com.insy2s.mskeycloak.dto.AccessDto;
import com.insy2s.mskeycloak.dto.CreateAccess;
import com.insy2s.mskeycloak.dto.mapper.IAccessMapper;
import com.insy2s.mskeycloak.model.Access;
import com.insy2s.mskeycloak.model.Role;
import com.insy2s.mskeycloak.repository.IRoleRepository;

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
import java.util.List;
import java.util.Optional;

@Slf4j
@EnableDiscoveryClient
@SpringBootApplication
@RequiredArgsConstructor
@EnableFeignClients
public class KeyCloakAuthServiceApplication {

    private final IRoleRepository roleRepository;
    private final IAccessService accessService;
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
        AccessDto menuAdmin = saveAccess("Administration", "Admin", "Menu", null, null, new ArrayList<>());
        AccessDto listUsers = saveAccess("Liste des utilisateurs", "users", "Page", "users", accessMapper.toEntity(menuAdmin), new ArrayList<>());
       // saveAccess("Ajouter un role", "add-role", "Page", "add-role", accessMapper.toEntity(menuAdmin), new ArrayList<>());
        AccessDto listRoles = saveAccess("Liste des roles", "roles", "Page", "roles", accessMapper.toEntity(menuAdmin), new ArrayList<>());
        saveAccess("Modifier un role", "updateRole", "Action", null, accessMapper.toEntity(listRoles), new ArrayList<>());
        saveAccess("Ajouter un role", "addRole", "Action", null, accessMapper.toEntity(listRoles), new ArrayList<>());
        saveAccess("supprimer un role", "deleteRole", "Action", null, accessMapper.toEntity(listRoles), new ArrayList<>());
        saveAccess("Modifier un utilisateur", "updateUser", "Action", null, accessMapper.toEntity(listUsers), new ArrayList<>());
        saveAccess("Ajouter un utilisateur", "addUser", "Action", null, accessMapper.toEntity(listUsers), new ArrayList<>());
        saveAccess("supprimer un utilisateur", "deleteUser", "Action", null, accessMapper.toEntity(listUsers), new ArrayList<>());
        AccessDto listGlobalAccess = saveAccess("Liste des accés", "access", "Page", "access", accessMapper.toEntity(menuAdmin), new ArrayList<>());
        saveAccess("Modifier un accés", "updateAccess", "Action", null, accessMapper.toEntity(listGlobalAccess), new ArrayList<>());
        saveAccess("Supprimer un accés", "deleteAccess", "Action", null, accessMapper.toEntity(listGlobalAccess), new ArrayList<>());
        saveAccess("Ajouter un accés", "addAccess", "Action", null, accessMapper.toEntity(listGlobalAccess), new ArrayList<>());

        //menu cours
        AccessDto menuCours = saveAccess("Cours", "Cours", "Menu", null, null, new ArrayList<>());
        AccessDto listCours = saveAccess("Liste des cours", "lstCours", "Page", "cours", accessMapper.toEntity(menuCours), new ArrayList<>());
        saveAccess("Ajouter un cours", "addCours", "Page", "ajoutcours", accessMapper.toEntity(menuCours), new ArrayList<>());
        saveAccess("Modifier un cours", "updateCourses", "Action", null, accessMapper.toEntity(listCours), new ArrayList<>());
        saveAccess("Ajouter un Cours", "addCourses", "Action", null, accessMapper.toEntity(listCours), new ArrayList<>());
        saveAccess("supprimer un Cours", "deleteCourses", "Action", null, accessMapper.toEntity(listCours), new ArrayList<>());
        //session
        AccessDto menuSession = saveAccess("Session", "Session", "Menu", null, null, new ArrayList<>());
        saveAccess("Ajouter une session", "addSession", "Page", "ajoutSession", accessMapper.toEntity(menuSession), new ArrayList<>());
        saveAccess("Ajouter un programme", "addProgram", "Page", "ajoutProgram", accessMapper.toEntity(menuSession), new ArrayList<>());
        saveAccess("Liste des programmes", "ListProgram", "Page", "listProgram", accessMapper.toEntity(menuSession), new ArrayList<>());
        AccessDto listSession=   saveAccess("Liste des sessions", "ListSession", "Page", "listSession", accessMapper.toEntity(menuSession), new ArrayList<>());

        saveAccess("Modifier séance", "updateSeance", "Action", null, accessMapper.toEntity(listSession), new ArrayList<>());

        saveAccess("Ajouter séance", "ajouterSeance", "Action", null, accessMapper.toEntity(listSession), new ArrayList<>());
        saveAccess("Supprimer séance", "deleteSeance", "Action", null, accessMapper.toEntity(listSession), new ArrayList<>());

        //menu projet
        AccessDto menuProjet = saveAccess("Projet", "Projet", "Menu", null, null, new ArrayList<>());
        AccessDto listProjet = saveAccess("Liste des projets", "lstProjets", "Page", "projets", accessMapper.toEntity(menuProjet), new ArrayList<>());
        saveAccess("Ajouter un projet", "createprojet", "Page", "createprojet", accessMapper.toEntity(menuProjet), new ArrayList<>());

        saveAccess("Suivre un projet", "suivi", "Page", "suivi", accessMapper.toEntity(menuProjet), new ArrayList<>());

        saveAccess("Suivre un projet", "suiviProject", "Action", null, accessMapper.toEntity(listProjet), new ArrayList<>());
        saveAccess("Modifier un Projet", "updateProject", "Action", null, accessMapper.toEntity(listProjet), new ArrayList<>());
        saveAccess("Ajouter un Projet", "addProject", "Action", null, accessMapper.toEntity(listProjet), new ArrayList<>());
        saveAccess("supprimer un Projet", "deleteProject", "Action", null, accessMapper.toEntity(listProjet), new ArrayList<>());

        //membre
        AccessDto menuMembre = saveAccess("Membre", "Membre", "Menu", null, null, new ArrayList<>());
        AccessDto listMembre = saveAccess("Liste des Membres", "membres", "Page", "membres", accessMapper.toEntity(menuMembre), new ArrayList<>());
        saveAccess("Ajouter un Membre", "ajout-membre", "Page", "ajout-membre", accessMapper.toEntity(menuMembre), new ArrayList<>());
        saveAccess("Modifier un Membre", "updateMember", "Action", null, accessMapper.toEntity(listMembre), new ArrayList<>());
        saveAccess("Liste des positions", "list-position", "Page", "list-position", accessMapper.toEntity(menuMembre), new ArrayList<>());

        //Eval
        AccessDto menueval = saveAccess("Evaluation", "Evaluation", "Menu", null, null, new ArrayList<>());
        saveAccess("Evaluation", "eval", "Page", "eval", accessMapper.toEntity(menueval), new ArrayList<>());
        saveAccess("Passer l'examen", "passer_examen", "Page", "passer_examen", accessMapper.toEntity(menueval), new ArrayList<>());
        saveAccess("Liste des quiz", "list_quiz_apprenant", "Page", "list_quiz_apprenant", accessMapper.toEntity(menueval), new ArrayList<>());
        saveAccess("Gérer quiz", "list_quiz", "Page", "list_quiz", accessMapper.toEntity(menueval), new ArrayList<>());

        saveAccess("Espace tuteur", "tuteur", "Page", "tuteur", accessMapper.toEntity(menueval), new ArrayList<>());

        //assistance
        AccessDto menuAssistance = saveAccess("Assistance", "Assistance", "Menu", null, null, new ArrayList<>());
        saveAccess("Liste des Assistances", "list-Assistance", "Page", "list-Assistance", accessMapper.toEntity(menuAssistance), new ArrayList<>());
        saveAccess("Demande d'aide", "demandeAide", "Page", "demandeAide", accessMapper.toEntity(menuAssistance), new ArrayList<>());
        //paramétre
        AccessDto menuParam = saveAccess("Paramétres", "Param", "Menu", null, null, new ArrayList<>());
        saveAccess("Liste des compétences", "skills", "Page", "skills", accessMapper.toEntity(menuParam), new ArrayList<>());
        saveAccess("Liste des thèmes","themeList","Page","themeList", accessMapper.toEntity(menuParam), new ArrayList<>());
        saveAccess("Type de phase", "typePhase", "Page", "typePhase", accessMapper.toEntity(menuParam), new ArrayList<>());

        //notification
        AccessDto menuNotification = saveAccess("Notification", "Notification", "Menu", null, null, new ArrayList<>());
        saveAccess("Liste des types de notification", "typelist", "Page", "typelist", accessMapper.toEntity(menuNotification), new ArrayList<>());
        saveAccess("Liste des notifications", "notificationss", "Page", "notificationss", accessMapper.toEntity(menuNotification), new ArrayList<>());
        saveAccess("Ajouter une notification", "addnotification", "Page", "addnotification", accessMapper.toEntity(menuNotification), new ArrayList<>());


//Forum
        AccessDto   menuForum=saveAccess("Forum","Forum","Menu",null, null, new ArrayList<>());
        saveAccess("Forum","forum-main","Page","forum-main", accessMapper.toEntity(menuForum), new ArrayList<>());


    }
    private AccessDto saveAccess(String name, String code, String type, String path, Access parent, List<Access> lst){
        AccessDto acc=accessService.findByCode(code);
        if(acc==null)
            return  accessService.create(new CreateAccess(name,code,type,path, parent, lst));
        return acc;
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
