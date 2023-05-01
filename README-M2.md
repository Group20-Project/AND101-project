<!-- (This is a comment) INSTRUCTIONS: Go through this page and fill out any **bolded** entries with their correct values.-->

# AND101 Milestone 2 - **Team/App Name**

Submitted by:
- **Luiz**
- **Evelise**
- **Wisdom**
- **Siddharth**
- **Tam**
- **Mike Brown**

Time spent: **10** hours spent in total

## Summary

This document provides a summary of our project building process for our app, **Spotify Album Info**

## Milestone Requirements

<!-- Please be sure to change the [ ] to [x] for any features you completed.  If a feature is not checked [x], you might miss the points for that item! -->

The following REQUIRED features are completed:

- [x] Assign features to each member of your group
- [x] Establish a goal time for completing each feature

The following REQUIRED files are included:

- [x] Updated 📄 `project_spec.md`, which contains:
  - [X] App Overview (Milestone 1)
  - [X] App Spec (Milestone 1)
  - [x] Checked off 2+ completed features
  - [x] 2+ GIFs of build progress

- [x] Our 🎥 Demo Video
  - [x] We have also added the Demo Video Link to the Group Info Form on the course portal.

The following EXTRA features are implemented:

- [x] List anything else that you added to improve your submission!
- [x] Implemented kotlin coroutines, suspend functions, and OkHttp Synchronous API calls so that the authentication token 
      can be retrieved before API calls are made. Due to the Codepath client being async making two separate requests does not guarantee order.
- [x] User can enter any Spotify album url. String is parsed to retrieve the album url, Spotify API call is made, and recyler is updated to new items from entered album.
- [x] Added listeners to all recycler items so that they open their respective url externally using intents. 
      ex.
	    holder.songImage.setOnClickListener {
            holder.songImage.context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(songList[position].getValue("albumURL"))))
            } 

## 🎥 Demo Video

Here's a video that demos all of the app's implemented features:

<img src='https://github.com/Group20-Project/AND101-project/tree/album/blob/main/api-demo.mp4' title='Video Demo' width='' alt='Video Demo' />

VIDEO created with **Google Pixel Screen Recorder**

## Notes

Here's a place for any other notes on this milestone!
