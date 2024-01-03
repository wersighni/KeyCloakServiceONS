package com.insy2s.KeyCloakAuth.service;


//import com.insy2s.KeyCloakAuth.ApiClient.MailingClient;
import com.insy2s.KeyCloakAuth.dto.AccessDto;
import com.insy2s.KeyCloakAuth.dto.MailDto;
import com.insy2s.KeyCloakAuth.model.*;
import com.insy2s.KeyCloakAuth.repository.AccessRepository;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.AccessTokenResponse;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;


@Service
public class LoginService {
	@Autowired
	UserService userService;
	@Autowired
	AccessRepository accessRepository;

	@Autowired
	IAccessService accessService;
@Value("${keycloak.server-url}")
private String serverUrl;

	@Value("${keycloak.realm}")
	private String realm;

	@Value("${keycloak.client-id}")
	private String clientId;

	@Value("${keycloak.client-secret}")
	private String clientSecret;

	@Value("${keycloak.admin-username}")
	private String userNameAdmin;

	@Value("${keycloak.admin-password}")
	private String passwordAdmin;
/*	public ResponseEntity<String> findAccount( String email ) {
		try {
			// Create a Keycloak Admin Client instance
			Keycloak keycloak = KeycloakBuilder.builder()
					.serverUrl(serverUrl)
					.realm(realm)
					.clientId(clientId)
					.password(passwordAdmin)
					.username(userNameAdmin)
					.clientSecret(clientSecret)
					.build();

			// Get the realm resource
			RealmResource realmResource = keycloak.realm(realm);
			List<UserRepresentation> user = realmResource.users().searchByEmail(email, true);
			if (user.isEmpty()) {
				return ResponseEntity.status(235).body("Vous n'avez pas de compte avec email : " + email);
			}
			else {
				String code=generateRandomCode();
				MailDto mailDto=new MailDto();
				mailDto.setTypeMail("restPasswordMail");
				mailDto.setBody(code);
				mailDto.setMailTo(email);
				mailDto.setSubject("Votre code de vérification de compte Upcofree");
				Map<String, List<String>> attributes = user.get(0).getAttributes();

				// Créer un nouvel attribut "VerificationCode" même si l'utilisateur n'a pas d'autres attributs
				if (attributes == null) {
					attributes = new HashMap<>();
				}

				// Ajouter l'attribut "VerificationCode" avec sa valeur
				attributes.put("VerificationCode", Arrays.asList(code));
				Date currentDate = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US);
				String currentDateAsString = dateFormat.format(currentDate);

				attributes.put("VerificationCodeDate", Arrays.asList(currentDateAsString));

				// Mettre à jour les attributs de l'utilisateur avec le nouvel attribut "VerificationCode"
				user.get(0).setAttributes(attributes);

				realmResource.users().get(user.get(0).getId()).update(user.get(0));

				boolean result =mailingClient.sendEmail(mailDto).getBody();
			if(result==true){
			return ResponseEntity.ok().body(" Code de vérification envoyé Vérifiez votre email :  " + email);

}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Error: " + e.getMessage());
		}
		return ResponseEntity.status(500).body("Error: ");
	}*/
	public static String generateRandomCode() {
		String characters = "0123456789";
		StringBuilder sb = new StringBuilder();
		SecureRandom random = new SecureRandom();

		int length = 8; // Longueur du mot de passe souhaitée

		for (int i = 0; i < length; i++) {
			int randomIndex = random.nextInt(characters.length());
			char randomChar = characters.charAt(randomIndex);
			sb.append(randomChar);
		}

		return sb.toString();
	}
	public ResponseEntity<String> testVerificationCode(String email, String code) {
		try {
			// Créer une instance du client administrateur Keycloak
			Keycloak keycloak = KeycloakBuilder.builder()
					.serverUrl(serverUrl)
					.realm(realm)
					.clientId(clientId)
					.password(passwordAdmin)
					.username(userNameAdmin)
					.clientSecret(clientSecret)
					.build();

			// Obtenir la ressource du realm
			RealmResource realmResource = keycloak.realm(realm);
			List<UserRepresentation> users = realmResource.users().searchByEmail(email, true);

			if (users.isEmpty()) {
				return ResponseEntity.status(235).body("Vous n'avez pas de compte avec l'email : " + email);
			}

			UserRepresentation user = users.get(0);
			Map<String, List<String>> attributes = user.getAttributes();

			if (attributes.containsKey("VerificationCode") && attributes.containsKey("VerificationCodeDate")) {
				List<String> codeVerif = attributes.get("VerificationCode");
				List<String> codeVerifTime = attributes.get("VerificationCodeDate");

				if (codeVerif != null && !codeVerif.isEmpty() && codeVerifTime != null && !codeVerifTime.isEmpty()) {
					String verificationCode = codeVerif.get(0);
					String verificationCodeDateString = codeVerifTime.get(0);

					SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US);
					try {
						Date verificationCodeDate = dateFormat.parse(verificationCodeDateString);
						Date currentDate = new Date();
						long differenceInMillis = currentDate.getTime() - verificationCodeDate.getTime();
						long differenceInMinutes = differenceInMillis / (60 * 1000);
						if(verificationCode.equals(code)){
						if (differenceInMinutes <= 30 && verificationCode.equals(code)) {
							return ResponseEntity.ok().body("code valid" );
						} else {
						return ResponseEntity.status(235).body("Le code de vérification a expiré.");
						}
						}
						 else {
							return ResponseEntity.status(235).body("Le code de vérification invalide.");
						}
					} catch (ParseException e) {
						e.printStackTrace();
						return ResponseEntity.status(235).body("Erreur lors de la conversion de la date.");
					}
				} else {
					return ResponseEntity.status(235).body("L'utilisateur a un attribut VerificationCode, mais sa valeur est vide.");
				}
			} else {
				return ResponseEntity.status(235).body("L'utilisateur n'a pas encore d'attribut VerificationCode.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Erreur : " + e.getMessage());
		}
	}


	public ResponseEntity resetPassword( String email ,  String password){
		try {
			// Create a Keycloak Admin Client instance
			Keycloak keycloakAdmin = KeycloakBuilder.builder()
					.serverUrl(serverUrl)
					.realm(realm)
					.clientId(clientId)
					.password(passwordAdmin)
					.username(userNameAdmin)
					.clientSecret(clientSecret)
					.build();

			// Get the realm resource
			RealmResource realmResource = keycloakAdmin.realm(realm);

			// Search for the user by email
			List<UserRepresentation> users = realmResource.users().searchByEmail(email, true);

			// If no user found, return a 404 response
			if (users.isEmpty()) {
				return ResponseEntity.status(404).body("Aucun utilisateur avec l'adresse e-mail : " + email);
			}

			// Update the user's password
			UserRepresentation user = users.get(0);
			CredentialRepresentation newCredential = toCredentialRepresentation(password);
			user.setCredentials(Collections.singletonList(newCredential));

			// Update the user in Keycloak
			try {
				// Update the user in Keycloak
				realmResource.users().get(user.getId()).update(user);
				return ResponseEntity.ok("Réinitialisation du mot de passe réussie");
			} catch (javax.ws.rs.NotFoundException notFoundEx) {
				return ResponseEntity.status(404).body("Aucun utilisateur avec l'adresse e-mail : " + email);
			} catch (javax.ws.rs.BadRequestException badRequestEx) {
				return ResponseEntity.status(400).body("Requête incorrecte lors de la mise à jour de l'utilisateur : " + badRequestEx.getMessage());
			} catch (javax.ws.rs.WebApplicationException webAppEx) {
				return ResponseEntity.status(500).body("Erreur lors de la mise à jour de l'utilisateur : " + webAppEx.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(500).body("Erreur : " + e.getMessage());
			}

		} catch (javax.ws.rs.NotFoundException notFoundEx) {
			return ResponseEntity.status(404).body("Aucun utilisateur avec l'adresse e-mail : " +email);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Erreur : " + e.getMessage());
		}
	}
	private CredentialRepresentation toCredentialRepresentation(String password) {
		CredentialRepresentation credential = new CredentialRepresentation();
		credential.setType(CredentialRepresentation.PASSWORD);
		credential.setValue(password);
		credential.setTemporary(false);
		return credential;
	}
public ResponseEntity changePassword(String username,String currentPassword,String newPassword)
{
	try {
		// Create a Keycloak Admin Client instance
		Keycloak adminKeycloak = KeycloakBuilder.builder()
				.serverUrl(serverUrl)
				.realm(realm)
				.clientId(clientId)
				.clientSecret(clientSecret)
				.username(userNameAdmin)
				.password(passwordAdmin)
				.build();

		// Get the realm resource for the desired realm
		RealmResource realmResource = adminKeycloak.realm(realm);

		// Check if the user exists by username
		List<UserRepresentation> users = realmResource.users().search(username);
		if (users.isEmpty()) {
			return ResponseEntity.badRequest().body("Utilisateur non trouvé.");
		}

		// Authenticate the user with their current password
		LoginRequest loginRequest=new LoginRequest(username,currentPassword);
		ResponseEntity loginResponse =login(loginRequest);
		if(loginResponse.getStatusCode().value()==401){
			return ResponseEntity.status(404).body("Mot de passe incorrecte .");

		}

		else{
			// If authentication is successful, reset the user's password
			UserResource userResource = realmResource.users().get(users.get(0).getId());
			CredentialRepresentation credentials = new CredentialRepresentation();
			credentials.setType(CredentialRepresentation.PASSWORD);
			credentials.setValue(newPassword);
			userResource.resetPassword(credentials);

			return ResponseEntity.ok().body("Mot de passe modifié avec succès.");
		}
	} catch (Exception e) {
		e.printStackTrace();
		return ResponseEntity.status(500).body("Erreur : " + e.getMessage());
	}
}


	public ResponseEntity login(LoginRequest loginrequest) {


		User user = null;

		if(!loginrequest.getUsername().equals("insy2s")) {

			  user = userService.getUser(loginrequest.getUsername());
			if (user == null) {
				return ResponseEntity.status(401).body(Collections.singletonMap("message", "Le nom d'utilisateur ou le mot de passe est incorrect"));

			}
		}
		// Create a new instance of LoginResponse to hold the response data
		LoginResponse loginResponse = new LoginResponse();

		// Configure Keycloak admin client
		Keycloak keycloak = KeycloakBuilder.builder()
				.serverUrl(serverUrl)           // Keycloak server URL
				.realm(realm)                   // Realm  name
				.username(loginrequest.getUsername()) // User's username for authentication
				.password(loginrequest.getPassword()) // User's password for authentication
				.clientId(clientId)             // Client ID of the application
				.clientSecret(clientSecret)     // Client secret for the application
				.grantType(OAuth2Constants.PASSWORD) // Use Resource Owner Password Credentials (ROPC) flow
				.build();

		// Perform the login and obtain an access token
		try {

			AccessTokenResponse accessTokenResponse = keycloak.tokenManager().grantToken(); // Attempt to obtain an access token
			loginResponse.setAccess_token(accessTokenResponse.getToken());// Set the access token in the response
			loginResponse.setRefresh_token(accessTokenResponse.getRefreshToken());   // Set the refresh token in the response


                if(user!=null)
				loginResponse.setAccess(accessService.findByUser(user.getId()));


			return ResponseEntity.ok().body(loginResponse);

		} catch (Exception e) {
			return ResponseEntity.status(401).body(" Le nom utilisateur ou le mot de passe est incorrect");
		}
	}



	private List<String> refactorAccess(List<Access> access){
		List<String> res=new ArrayList<String>();
		for(Access a : access){
			if(!res.contains(a.getCode()))
			{
				res.add(a.getCode());
			}
		}

		return res;
	}

	private List<Access> refactorMenu(List<Access> access,LoginResponse loginResponse){
		List<Access> res=new ArrayList<Access>();
		for(Access a : access){
			if(!res.contains(a))
			{
				res.add(a);
			}
		}

		return res;
	}
	public ResponseEntity<String> logout(String userId) {
		// Create a Keycloak instance for logout
		Keycloak keycloakAdmin = KeycloakBuilder.builder()
				.serverUrl(serverUrl)       // Keycloak server URL
				.realm(realm)               // Realm (security domain) name
				.clientId(clientId)
				.username(userNameAdmin)
				.password(passwordAdmin)
				.clientSecret(clientSecret) // Client secret for the application
				.build();

		// Perform the logout using the provided access token

		try {
			RealmResource realmResource = keycloakAdmin.realm(realm);

			UserResource users = realmResource.users().get(userId);
			users.logout();

			return ResponseEntity.ok().body("Déconnexion réussie, merci de votre confiance !");
		}
		 catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.ok().body("Échec de la déconnexion");
		}}}
