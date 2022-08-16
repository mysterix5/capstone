# Vover

<img align="right" width="300" src="https://user-images.githubusercontent.com/28150646/184717435-95dbd022-cda1-4c74-9d08-90af28772f61.gif">

This is my capstone (final) project from the 'neue fische' Java full stack Bootcamp. 

With Vover you can create an amazing roboter-like voice messages with the voices of your
friends. 

## Usage guide
### Vover messages
Enter a text you want to send as audio to your friends and click send.

The words of your text appear again as select menus. You can see if the words you entered are
all allowed and then choose from a list of recordings with the information about creator and
tag. Each recording is colored with the following meaning: 

![#008609](https://via.placeholder.com/15/008609/008609.png) recording is in scope  
![#a7e006](https://via.placeholder.com/15/a7e006/a7e006.png) recording is from a friend but not in scope  
![#d39104](https://via.placeholder.com/15/d39104/d39104.png) a user who is not your friend recorded this  
![#0930b0](https://via.placeholder.com/15/0930b0/0930b0.png) you recorded this yourself  
![#b43535](https://via.placeholder.com/15/b43535/b43535.png) not available, record the word!  
![#881111](https://via.placeholder.com/15/881111/881111.png) invalid, only letters are allowed

### Friends and scope
On your Userpage (upper right corner) there is a friends section. You can connect with other
users here by sending them a friendship request. After they accept it in the same place your
friends recordings will automatically be preferred and you can use their recordings with
accessibility 'FRIENDS'. 

On top of the main page you can choose a selection of your friends as your scope to prefer
their recordings this time.

### Recording
Record a word and add it to the database. You must fill in the following metadata for each
record:  

**word** which word did you record  
**tag** add a fitting tag like 'normal', 'accent' or 'funny'  
**accessibility** decide if everyone, only your friends or only yourself can use this recording in their messages

There are different ways to get to the record page: 
- Click on the record symbol in the upper left corner; you can simply record a few words here and navigate to another place afterwards.
- After submitting a text open one of the word select menus and click on record. You are navigated to a special record page where you can record this single word and back to the main page with your previously entered text.
- After submitting a text click on the 'record missing words' or 'record all words' button. Just like in the case before you are navigated to the special record page and can record all the words you need comfortably in a row

You can hear again, edit and delete your recordings on the user page. 

### History
Also on your user page there is a 'History' page. Here you can see your last requested audios
and by clicking on a history element you are navigated to the main page with the exact same text
and recordings selected like the last time you requested this. 

### Hints
- You can always click on the Vover title to get to the main page. 
- The url of a history item can be shared
- If you are not satisfied with your recording just press the record button again. The last recording is overwritten. 

## Technical
The tech stack of Vover is

![image](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot)
![image](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![image](https://img.shields.io/badge/TypeScript-007ACC?style=for-the-badge&logo=typescript&logoColor=white)
![image](https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white)
<img src="https://upload.wikimedia.org/wikipedia/commons/4/4b/FFmpeg-Logo.svg" height="25">
![image](https://img.shields.io/badge/Material%20UI-007FFF?style=for-the-badge&logo=mui&logoColor=white)
![image](https://img.shields.io/badge/Heroku-430098?style=for-the-badge&logo=heroku&logoColor=white)
![image](https://img.shields.io/badge/IntelliJ_IDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)
![image](https://img.shields.io/badge/Junit5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![image](https://img.shields.io/badge/Nextcloud-0082C9?style=for-the-badge&logo=Nextcloud&logoColor=white)

### Datastructures
Vover uses a MongoDB and a private nextcloud storage to save the audio files.  
The most basic entity in Vover is a record. A record has the following properties: 
- word
- creator
- tag
- accessibility
- an url where to find the real audio file in the cloud storage

The creator matches the username of a registered user and is set automatically from the Spring Boot principal.  
Further more there are some user details in an own MongoDB collection. They include
- a list of friends
- a list of friend requests (not yet accepted)
- a list of received friend requests (you didn't yet accept)
- the scope, which is a subset of the friends
- history entries

And of course the users are saved in their own collection. 

### Main functionality
What happens when a user submits a text to get back the dropdown select choices with the different recordings?

A text is submitted as string to the backend. What is the desired result? 
We want the words in the text splitted and for each word we want information if the word is generally available 
and we want to be able to choose from a selection of recordings. Each of the recordings should also hold some information 
including the creator the tag and the record availability, which means is the recording from a user in the scope, 
is the user a friend, ... as explained in the usage guide part. 

The following data structure is used to transfer this information and assumed to be optimal.

- textWords: An array of strings, the text splitted into the words
- wordRecordMap: a map, holding for each word an array of record metadata. The md includes word, creator, tag, availability, the database id. This way, we need to transfer the recording metadata only once for each word, no matter how often it appears in the initial text.
- defaultIds: the choice, which recording is preselected

What happens in the backend?  
The text is splitted into words with a regex. Then, each word is checked with a regex. 
The valid words are added to a set and for each word the record metadata for all recordings the user is allowed to get is fetched with one request from the db. 
From this the availability of the word can be computed looping over all words:  
- the word is not in the set -> invalid
- the word has no db information -> unavailable
- the word has db information -> available

For each record also an availability is computed. It is one of scope, friends, public, myself. To compute this the recordings creator simply has to be compared with the requesting users friends, scope, ... . This list is also easily sorted based on this values. 

In frontend the user sees the select menus for each word. The select choices are colored based on the record availability. 

After selecting the desired recordings the second big request is submitted by clicking on the "get audio" button. 
A list of recording ids is send to the backend. 
In backend the recording metadata is fetched from MongoDB. 
It is checked if the user is allowed to access all of these recordings. 
Then the audio data are loaded from the nextcloud storage. 
These requests are performed in parallel to avoid a sequentiell addition of the cloud loading time. 

The audio data is then merged with the Jaffree java library which is a wrapper for FFmpeg. 
FFmpeg is a system tool for audio processing and called as external tool from java. 

### Userpage
#### Recordings
Here the user can manage the own recordings. Fetching the recordings is realized with pagination. 
Here is a very straight forward implementation of editing the record data. 

#### History
When requesting a merged audio, additionally a history entity is saved in the db in his own collection. 
A history entry consists of the text as string, and the recording choices as List of ids. 
When requesting a history element again it is requested by id and this id is part of the url. 
This way a history url can even be shared and called with another Vover account. 
