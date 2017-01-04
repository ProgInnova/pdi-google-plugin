package com.proginnova.gdrive.impl;


import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.naming.LimitExceededException;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.Drive.Permissions;
import com.google.api.services.drive.Drive.Permissions.Create;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.User;

@SuppressWarnings("unused")
public class DriveFileManagement {
	
	private static String FILE_SET_FIELDS_PARAMETER = "nextPageToken, files(id, name, parents, mimeType, quotaBytesUsed, owners, permissions)";
	
	// https://developers.google.com/drive/v3/web/migration
	
	public static File copyFiles(Drive service, String originFile, String parentFolderId, String copyFileName) throws IOException, LimitExceededException{
		About accountData = service.about().get().setFields("storageQuota").execute();
		File fileToCopy = getFile(service, originFile, false);
		if(fileToCopy != null && fileToCopy.getQuotaBytesUsed() + accountData.getStorageQuota().getUsage() <= accountData.getStorageQuota().getLimit() ){
			File file = new File();
			file.setName(copyFileName);
			List<String> parents = new LinkedList<>();
			parents.add(parentFolderId);
			file.setParents(parents);
			File copiedFile = service.files().copy(fileToCopy.getId(), file).setFields("id, owners, permissions").execute();
			
			User owner = fileToCopy.getOwners().get(0);
			for(Permission permission:copiedFile.getPermissions()){
				if(permission.getEmailAddress().equals(owner.getEmailAddress()) && !permission.getRole().equals(DriveRolePermission.owner.toString()) ){
					Permission newPermission = new Permission();
					newPermission.setId(permission.getId());
					newPermission.setRole("owner");
					service.permissions().update(copiedFile.getId(), permission.getId(), newPermission).setFields("role").setTransferOwnership(true).execute();
				}
			}
			return copiedFile;
		}else if(fileToCopy != null){
			throw new LimitExceededException("Quota exceeded in this account");
		}else{
			throw new IOException("File to copy not found");
		}
	}
	
	public static File copyFiles(Drive service, File originFile, String parentFolderId, String copyFileName) throws IOException, LimitExceededException{
		About accountData = service.about().get().setFields("storageQuota").execute();
		if(originFile != null && originFile.getQuotaBytesUsed() + accountData.getStorageQuota().getUsage() <= accountData.getStorageQuota().getLimit() ){
			File file = new File();
			file.setName(copyFileName);
			List<String> parents = new LinkedList<>();
			parents.add(parentFolderId);
			file.setParents(parents);
			File copiedFile = service.files().copy(originFile.getId(), file).setFields("id, owners, permissions").execute();
			
			User owner = originFile.getOwners().get(0);
			for(Permission permission:copiedFile.getPermissions()){
				if(permission.getEmailAddress().equals(owner.getEmailAddress()) && !permission.getRole().equals(DriveRolePermission.owner.toString()) ){
					Permission newPermission = new Permission();
					newPermission.setId(permission.getId());
					newPermission.setRole("owner");
					service.permissions().update(copiedFile.getId(), permission.getId(), newPermission).setFields("role").setTransferOwnership(true).execute();
				}
			}
			return copiedFile;
		}else if(originFile != null){
			throw new LimitExceededException("Quota exceeded in this account");
		}else{
			throw new IOException("File to copy not found");
		}
	}
	
	public static File copyFiles(Drive service, File originFile, File parentFolder, String copyFileName) throws IOException, LimitExceededException{
		About accountData = service.about().get().setFields("storageQuota").execute();
		if(originFile != null && originFile.getQuotaBytesUsed() + accountData.getStorageQuota().getUsage() <= accountData.getStorageQuota().getLimit() ){
			File file = new File();
			file.setName(copyFileName);
			List<String> parents = new LinkedList<>();
			parents.add(parentFolder.getId());
			file.setParents(parents);
			File copiedFile = service.files().copy(originFile.getId(), file).setFields("id, owners, permissions").execute();
			
			User owner = originFile.getOwners().get(0);
			for(Permission permission:copiedFile.getPermissions()){
				if(permission.getEmailAddress().equals(owner.getEmailAddress()) && !permission.getRole().equals(DriveRolePermission.owner.toString()) ){
					Permission newPermission = new Permission();
					newPermission.setId(permission.getId());
					newPermission.setRole("owner");
					service.permissions().update(copiedFile.getId(), permission.getId(), newPermission).setFields("role").setTransferOwnership(true).execute();
				}
			}
			return copiedFile;
		}else if(originFile != null){
			throw new LimitExceededException("Quota exceeded in this account");
		}else{
			throw new IOException("File to copy not found");
		}
	}
	
	public static File copyFilesWithId(Drive service, String originFileId, String parentFolderId, String copyFileName) throws IOException, LimitExceededException{
		About accountData = service.about().get().setFields("storageQuota").execute();
		File fileToCopy = service.files().get(originFileId).setFields(FILE_SET_FIELDS_PARAMETER).execute();
		if(fileToCopy != null && fileToCopy.getQuotaBytesUsed() + accountData.getStorageQuota().getUsage() <= accountData.getStorageQuota().getLimit() ){
			File file = new File();
			file.setName(copyFileName);
			List<String> parents = new LinkedList<>();
			parents.add(parentFolderId);
			file.setParents(parents);
			File copiedFile = service.files().copy(fileToCopy.getId(), file).setFields("id, owners, permissions").execute();
			User owner = fileToCopy.getOwners().get(0);
			for(Permission permission:copiedFile.getPermissions()){
				if(permission.getEmailAddress().equals(owner.getEmailAddress()) && !permission.getRole().equals(DriveRolePermission.owner.toString()) ){
					Permission newPermission = new Permission();
					newPermission.setId(permission.getId());
					newPermission.setRole("owner");
					service.permissions().update(copiedFile.getId(), permission.getId(), newPermission).setFields("role").setTransferOwnership(true).execute();
				}
			}
			return copiedFile;
		}else if(fileToCopy != null){
			throw new LimitExceededException("Quota exceeded in this account");
		}else{
			throw new IOException("File to copy not found");
		}
	}
	
	
	public static File createOrGetFolder(Drive service, String folderName, String folderDestiny) throws IOException{
		File parentFolder = getFile(service, DriveFileMimeTypes.FOLDER_MIME_TYPE, folderDestiny);
		if(parentFolder == null){
			throw new IOException(String.format("Folder %s not found", folderDestiny));
		}
		File folder = getFile(service, DriveFileMimeTypes.FOLDER_MIME_TYPE, folderName);
		if(folder != null){
			return folder;
		}else{
			folder = new File();
			folder.setName(folderName);
			folder.setMimeType(DriveFileMimeTypes.FOLDER_MIME_TYPE);
			List<String> parents = new LinkedList<>();
			parents.add(parentFolder.getId());
			folder.setParents(parents);
			File createdFolder = service.files().create(folder).setFields("id, owners, permissions").execute();
			
			User owner = parentFolder.getOwners().get(0);
			for(Permission permission:createdFolder.getPermissions()){
				if(permission.getEmailAddress().equals(owner.getEmailAddress())){
					Permission newPermission = new Permission();
					newPermission.setId(permission.getId());
					newPermission.setRole("owner");
					service.permissions().update(createdFolder.getId(), permission.getId(), newPermission).setFields("role").setTransferOwnership(true).execute();
				}
			}
			
			return createdFolder;
		}
	}
	
	public static File getFile(Drive service, String file, boolean fileIsId) throws IOException{
		String pageToken = null;
		FileList request = null;
		if(!fileIsId){
			do{
				request = service.files().list().setQ(String.format("trashed=false and name='%s'", file)).setPageToken(pageToken).setFields(FILE_SET_FIELDS_PARAMETER).execute();
				for(File fileIt: request.getFiles()){
					if(fileIt.getName().equals(file)){
						return fileIt;
					}
				}
				pageToken = request.getNextPageToken();
			}while(pageToken != null);
		}else{
			return service.files().get(file).setFields(FILE_SET_FIELDS_PARAMETER).execute();
		}
		return null;
	}
	
	public static File getFile(Drive service, String mimeType, String file) throws IOException{
		String pageToken = null;
		FileList request = null;
		do{
			request = service.files().list().setQ(String.format("mimeType='%s' and trashed=false and name='%s'", mimeType, file)).setPageToken(pageToken).setFields(FILE_SET_FIELDS_PARAMETER).execute();
			for(File fileIt: request.getFiles()){
				if(fileIt.getName().equals(file)){
					return fileIt;
				}
			}
			pageToken = request.getNextPageToken();
		}while(pageToken != null);
		return null;
	}
	
	public static File getFileWithParent(Drive service, String fileName, String parent) throws IOException{
		String pageToken = null;
		FileList request = null;
		do{
			request = service.files().list().setQ(String.format("trashed=false and name='%s' and '%s' in parents", fileName, parent)).setPageToken(pageToken).setFields(FILE_SET_FIELDS_PARAMETER).execute();
			for(File file: request.getFiles()){
				if(file.getName().equals(fileName)){
					return file;
				}
			}
			pageToken = request.getNextPageToken();
		}while(pageToken != null);
		return null;
	}
	
	public static File getFileWithParent(Drive service, String mimeType, String fileName, String parent) throws IOException{
		String pageToken = null;
		FileList request = null;
		do{
			request = service.files().list().setQ(String.format("mimeType='%s' and trashed=false and name='%s' and '%s' in parents", mimeType, fileName, parent)).setPageToken(pageToken).setFields(FILE_SET_FIELDS_PARAMETER).execute();
			for(File file: request.getFiles()){
				if(file.getName().equals(fileName)){
					return file;
				}
			}
			pageToken = request.getNextPageToken();
		}while(pageToken != null);
		return null;
	}
	
	public static boolean addUserPermission(Drive service, File driveFile, String emailAccount, DriveRolePermission rolePermission) throws IOException{
		Permission permission = new Permission();
		permission.setEmailAddress(emailAccount);
		permission.setRole(rolePermission.toString());
		permission.setType("user");
		Create buildRequest = service.permissions().create(driveFile.getId(), permission).setFields("id, role");
		if(rolePermission.equals(DriveRolePermission.owner)){
			buildRequest.setTransferOwnership(true);
		}
		Permission createdPermission = buildRequest.execute();
		System.out.println(createdPermission);
		return createdPermission.getId() != null && createdPermission.getRole().equals(rolePermission.toString());
	}
	
	public static boolean addUserPermission(Drive service, File driveFile, String emailAccount, DriveRolePermission rolePermission, boolean notifyByEmail) throws IOException{
		Permission permission = new Permission();
		permission.setEmailAddress(emailAccount);
		permission.setRole(rolePermission.toString());
		permission.setType("user");
		Create buildRequest = service.permissions().create(driveFile.getId(), permission).setFields("id, role").setSendNotificationEmail(notifyByEmail);
		if(rolePermission.equals(DriveRolePermission.owner)){
			buildRequest.setTransferOwnership(true);
		}
		Permission createdPermission = buildRequest.execute();
		System.out.println(createdPermission);
		return createdPermission.getId() != null && createdPermission.getRole().equals(rolePermission.toString());
	}
	
	public static boolean addUserPermission(Drive service, File driveFile, String emailAccount, DriveRolePermission rolePermission, String emailMessage) throws IOException{
		Permission permission = new Permission();
		permission.setEmailAddress(emailAccount);
		permission.setRole(rolePermission.toString());
		permission.setType("user");
		Create buildRequest = service.permissions().create(driveFile.getId(), permission).setFields("id, role").setEmailMessage(emailMessage);
		if(rolePermission.equals(DriveRolePermission.owner)){
			buildRequest.setTransferOwnership(true);
		}
		Permission createdPermission = buildRequest.execute();
		System.out.println(createdPermission);
		return createdPermission.getId() != null && createdPermission.getRole().equals(rolePermission.toString());
	}
	
	public static boolean addAnyoneAccess(Drive service, File driveFile, DriveRolePermission role) throws IOException{
		if(role.equals(DriveRolePermission.owner)){
			throw new IllegalArgumentException("Can't be owner");
		}
		
		for(Permission permission:driveFile.getPermissions()){
			if(permission.getType().equals("anyone")){
				return true;
			}
		}
		Permission permission = new Permission();
		permission.setType("anyone");
		permission.setRole(role.toString());
		service.permissions().create(driveFile.getId(), permission).setFields("id").setSendNotificationEmail(false).execute();
		return true;
	}
	
	public static boolean existsFile(Drive service, String mimeType, String fileName) throws IOException{
		return getFile(service, mimeType, fileName) != null;
	}
	
	public static boolean existsFile(Drive service, String fileName) throws IOException{
		return getFile(service, fileName, false) != null;
	}
	
	public static boolean deleteAllFoldersInFolder(Drive service, String folderParent) throws IOException{
		String pageToken = null;
		FileList request = null;
		do{
			request = service.files().list().setQ(String.format("mimeType='%s' and '%s' in parents", DriveFileMimeTypes.FOLDER_MIME_TYPE, folderParent)).setPageToken(pageToken).setFields(FILE_SET_FIELDS_PARAMETER).execute();
			System.out.println(request.getFiles());
			for(File file: request.getFiles()){
				service.files().delete(file.getId()).execute();
			}
			pageToken = request.getNextPageToken();
		}while(pageToken != null);
		return false;
	}
	
}
