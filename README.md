# PDI Google Drive Steps plugin #

Development stage

### Authentication and service account ###
This plugin works with the [OAuth2 service accounts](https://developers.google.com/identity/protocols/OAuth2ServiceAccount) from Google, to create your own service account with a project at [Google APIs Console](https://console.developers.google.com)

### Building ###
This plugin is built with Maven
```shell
$ git clone ...
$ cd ...
$ mvn package
```

## Step: Copy files in Drive ![][1] ##

This step, copies a specific file shared with the service account and replicate it according to the number of rows received. Requires a new title name to each copy. It's perfect if you have a template file and wants to replicate them taking advantage to the data you have, also outputs the fileId of the replicated files, in case you needed.

### Step configuration ###
| Property 		| Description 	|
| ------------- | ------------- |
| Title by field | Title field to replicate and rename the file to copy |
| Service account email | Email generated with the service account, provided by Google at [Google APIs Console](https://console.developers.google.com) |
| Service account keyfile | JSON or P12 file generated with the service account, provided by Google at [Google APIs Console](https://console.developers.google.com) |
| File to copy | File ID from the file to be replicated. This file must be in Google Drive created previewsly and shared with the service account email |
| Folder to dump | Folder ID from the folder to dump all the replicas created with this step. This folder must be in Google Drive created previewsly and shared with the service account email |
| Impersonate user | Only if the owner it's a G Suite Business account, just requires the email to impersonate. Check the next section to configure it. |

#### Impersonate user (G Suite accounts only) ####

Accoding to the Google Drive API, if the owner account it's a G Suite Business account, **must** impersonate the account and enable [G Suite Domain-Wide Delegation](https://developers.google.com/admin-sdk/directory/v1/guides/delegation) to your service account, to do that you must follow the following steps in the Admin console.

At the end of the process, make sure that you enabled this scopes:

* https://www.googleapis.com/auth/drive 
* https://www.googleapis.com/auth/userinfo.email 
* https://www.googleapis.com/auth/userinfo.profile


### Permissions and sharing ###

There are 3 posible configurations to allow users to access the replicated file in the step.

* Input: enables this account to all replicated files, it's configurable in the step and supports parameters in the account field. Also, if you wish, you can notify by email and notify with a custom message.
* Field: enables this account to their corresponding copied file in the row field. Also enables the posibility to notify to each account and notify by a custom message, this custom message it's also required to be ready in the row and specified by field.
* Any: enables the copied files to be shared with anyone that has the file url.

By each configuration, you have a separated role assignment (reader, commenter or writter).




[1]:src/main/resources/drivecopy.svg