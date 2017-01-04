package com.proginnova.gdrive.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.Collection;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.common.io.Files;

public class GoogleConnection {
	private Credential credential = null;
	private String APPLICATION_NAME = "pentaho-drive-plugin/1.0";
	private Collection<String> scopes;
	
	public GoogleConnection(String credentialFile, String serviceEmail, Collection<String> scopes) throws Exception{
		credential = authorize(credentialFile, serviceEmail, scopes);
		if(credential == null){
			throw new Exception("Credential not created");
		}
		this.scopes = scopes;
	}
	
	
	// https://developers.google.com/admin-sdk/directory/v1/guides/delegation#create_the_service_account_and_its_credentials
	// Only for Non gmail domain accounts
	public GoogleConnection(String credentialFile, String serviceEmail, String impersonateAccount, Collection<String> scopes) throws Exception{
		credential = authorize(credentialFile, serviceEmail, impersonateAccount ,scopes);
		if(credential == null){
			throw new Exception("Credential not created");
		}
		this.scopes = scopes;
	}
	
	private Credential authorize(String credentialFile, String serviceEmail, Collection<String> scopes) throws Exception{
		File file = new File(credentialFile);
		if(!file.exists()){
			throw new FileNotFoundException(String.format("File %s not found", credentialFile));
		}
		HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		GoogleCredential googleCredential = null;
		if(file.getName().endsWith(".p12")){
			String p12Content = Files.readFirstLine(file, Charset.defaultCharset());
			if(p12Content.startsWith("Enter ")){
				throw new Exception(String.format("Not accepted content: %s", p12Content));
			}
			googleCredential = new GoogleCredential.Builder()
					.setTransport(transport)
					.setJsonFactory(jsonFactory)
					.setServiceAccountId(serviceEmail)
					.setServiceAccountScopes(scopes)
					.setServiceAccountPrivateKeyFromP12File(file)
					.build();
		}else if(file.getName().endsWith(".json")){
			googleCredential = GoogleCredential.fromStream(new FileInputStream(file)).createScoped(scopes);
		}else{
			throw new UnsupportedOperationException(String.format("File %s extension not supported, only .json or .p12", file.getName()));
		}
		return googleCredential;
	}
	
	private Credential authorize(String credentialFile, String serviceEmail, String impersonateAccount, Collection<String> scopes) throws Exception{
		File file = new File(credentialFile);
		if(!file.exists()){
			throw new FileNotFoundException(String.format("File %s not found", credentialFile));
		}
		HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		GoogleCredential googleCredential = null;
		if(file.getName().endsWith(".p12")){
			String p12Content = Files.readFirstLine(file, Charset.defaultCharset());
			if(p12Content.startsWith("Enter ")){
				throw new Exception(String.format("Not accepted content: %s", p12Content));
			}
			try{
				googleCredential = new GoogleCredential.Builder()
						.setTransport(transport)
						.setJsonFactory(jsonFactory)
						.setServiceAccountId(serviceEmail)
						.setServiceAccountScopes(scopes)
						.setServiceAccountPrivateKeyFromP12File(file)
						.setServiceAccountUser(impersonateAccount)
						.build();
			}catch(TokenResponseException ex){
				System.err.println(ex.getMessage());
			}
		}else if(file.getName().endsWith(".json")){
			googleCredential = GoogleCredential.fromStream(new FileInputStream(file)).createScoped(scopes);
			if(googleCredential != null){
				googleCredential = new GoogleCredential.Builder()
						.setTransport(transport)
						.setJsonFactory(jsonFactory)
						.setServiceAccountId(googleCredential.getServiceAccountId())
						.setServiceAccountScopes(googleCredential.getServiceAccountScopes())
						.setServiceAccountPrivateKey(googleCredential.getServiceAccountPrivateKey())
						.setServiceAccountUser(impersonateAccount)
						.build();
			}else{
				throw new NullPointerException("Expected json file");
			}
		}else{
			throw new UnsupportedOperationException(String.format("File %s extension not supported, only .json or .p12", file.getName()));
		}
		return googleCredential;
	}
	
	public boolean isConnected(){
		return credential != null;
	}
	
	public Drive getDrive(){
		if(scopes.contains(DriveScopes.DRIVE)){
			return new Drive.Builder(credential.getTransport(), credential.getJsonFactory(), credential).setApplicationName(APPLICATION_NAME).build();
		}
		throw new UnsupportedOperationException("Drive scope not found");
	}
	
	
}
