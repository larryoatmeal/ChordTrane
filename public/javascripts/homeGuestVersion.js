$(document).ready(function(){
  // $( "#eq > span" ).each(function() {
 //      // read initial vals from markup and remove that
 //      var val = toInt( $( this ).text());
 //      $( this ).empty().slider({
 //        val: val,
 //        range: "min",
 //        animate: true,
 //        orientation: "vertical"
 //      });
 //    });

  var GUEST_USER_ID = 1
  var songIndex = 0 //PlaybackSettingsList array must remain lockstep with song. Indexes will match up
  var songs
  var song //current song


  //Playback Settings
  var playbackSettingsList = []
  var playbackSettings //current settings
  // var bpm
  // var repeats
  // var pianoMin
  // var pianoMax
  // var pianoStayInTessitura
  // var pianoConnectivity
  // var bassMin
  // var bassMax
  // var bassStayInTessitura
  // var bassConnectivity

  //MIDI
  var player 

  var midipath
  var charactersInLongestLine
  var renderedText


  initialize()
  //initializeMidiJs()

  function initialize(){
    getSongs(loadSong)
    //songIndex starts at 0
  }
  
  // SONG CRUD ------------------------------------------------------------------------------


  function getSongs(loadFunction){//This function fired only once. All songs are stored in javascript memory
    jsRoutes.controllers.JSONmaster.getSongsFull(GUEST_USER_ID).ajax(
      {
        success: function(data){
          songs = data
          populateSongDropdown()
          //console.log(data)

          var len = songs.length
          // for( var i = 0; i < len; i++){//Get playback settings as well
          //   getPlaybackSettings(songs[i].id)
          // }


          function getAllPlaybackSettings(index){
            if(index < len){
              getPlaybackSettings(songs[index].id, function(){getAllPlaybackSettings(index + 1)})//second function happens after first
            }else{
              console.log(songs)
              console.log(playbackSettingsList)
              loadFunction()
            }
          }

          getAllPlaybackSettings(0)


        },
        error: function(){
        }
      }
    )
  }

  function newSong(){
    var blankSong={
      id: -1, //doesn't really matter, not stored
      rawText: "|C|",
      title: "New Song", 
      composer: "Composer",
      dateCreated: "Date", 
      timeSig: 4, 
      currentKey: "C", 
      destinationKey: "C",
      transposeOn: false,
      romanNumeral: false,
      userId: -1 //doesn't matter
    }
    songs.push(blankSong)
    newPlaybackSettings()

    populateSongDropdown()//refresh dropdown list

    //Select dropdown of new song

    songIndex = songs.length - 1
    $("#songDropdown").val(songIndex)

    loadSong()

  }
  function deleteSong(){
    songs.splice(songIndex, 1)
    playbackSettingsList.splice(songIndex, 1)
    populateSongDropdown()//refresh dropdown list
    songIndex = 0 //reset song to first song
    loadSong()
  }
  function saveSong(){
    var title = $("#title").val()
    var composer = $("#composer").val()
    var dateCreated = $("#dateCreated").val()
    var timeSig = parseInt($("#timeSig").val(), 10)
    var currentKey = $("#currentKey").val()
    var destinationKey = $("#destinationKey").val()
    var transposeOn = $("#transposeOn").is(':checked')
    var romanNumeral = $("#romanNumeral").is(':checked')
    var rawText = $("#rawText").val()

    var updatedSong={
      id: song.id,
      rawText: rawText,
      title: title, 
      composer: composer,
      dateCreated: dateCreated, 
      timeSig: timeSig, 
      currentKey: currentKey, 
      destinationKey: destinationKey,
      transposeOn: transposeOn,
      romanNumeral: romanNumeral,
      userId: -1};


    $("#songDropdown option:selected").text(title)
    songs[songIndex] = updatedSong
    savePlaybackSettings()
    loadSong()
  }

  function loadSong(){//Fire when songIndex changes
    song = songs[songIndex]
    //console.log(songIndex)
    console.log(song)
    //alert(song.title)
    updateSongFieldDisplay()
    printFormattedText()

    loadPlaybackSettings()
    updatePlaybackSettingsFieldDisplay()

  }

  //SONG Rendering/Display ---------------------------------------------------------------
  function populateSongDropdown(){
    $("#songDropdown").empty()//clear first

    for (var i = 0; i < songs.length; i++){

      $("#songDropdown").append(
        $('<option>').text(songs[i].title).val(i) //Store index in value
      )
    }
  }

  function updateSongFieldDisplay(){
    $("#titleDisplay").text(song.title)
    $("#title").val(song.title)
    $("#composer").val(song.composer)
    $("#dateCreated").val(song.dateCreated)
    $("#timeSig").val(song.timeSig)
    $("#currentKey").val(song.currentKey)
    $("#destinationKey").val(song.destinationKey)
    $("#transposeOn").attr('checked', song.transposeOn);
    $("#romanNumeral").attr('checked', song.romanNumeral);
    $("#rawText").val(song.rawText)
  }

  function printFormattedText(){
    var songJSON = JSON.stringify(song)
    jsRoutes.controllers.JSONmaster.formatText(songJSON).ajax(
    {
      success: function(data){
        //charactersInLongestLine = data.split(/\r\n|\r|\n/g).map(function(value){return value.length}).sort(function(a,b){return b-a})[0]
        renderedText = data

        var formatted = grayOutSlashes(data)
        //console.log(formatted)
      
        $("#renderedText").html(formatted)
      },
      error: function(error){
        $("#renderedText").html("Error")
      },
      data: songJSON,
      contentType: "application/json"
    }
    )
  }

  function grayOutSlashes(raw){
    return raw.replace(/\//g, '<span class="slashchord">/</span>')

    //First surround each line in <span class="renderedLine"></span>
    // return slashed.split(/\r\n|\r|\n/g).map(
    //   function(value){
    //     return "<div class=\"renderedLine\">" + value + "</div>"
    //   }
    //   ).join("")
  }


  //Playback Settings CRUD ---------------------------------------------------------------
  function getPlaybackSettings(id, nextCall){
    jsRoutes.controllers.JSONmaster.getPlaybackSettings(id).ajax({
      success: function(data){
        console.log(data)
        playbackSettingsList.push(data)
        nextCall()
      },
      error: function(err){
        alert("Error opening playbackSettings")
      }
    })
  }

  //Playback Settings DISPLAY ---------------------------------------------------------------
  function updatePlaybackSettingsFieldDisplay(){
    $("#bpm").val(playbackSettings.bpm)
    $("#repeats").val(playbackSettings.repeats)
    $("#pianoMin").val(playbackSettings.pianoSettings.lower)
    $("#pianoMax").val(playbackSettings.pianoSettings.upper)
    $("#pianoStayInTessitura").val(playbackSettings.pianoSettings.stayInTessitura)
    $("#pianoConnectivity").val(playbackSettings.pianoSettings.connectivity)
    $("#bassMin").val(playbackSettings.bassSettings.lower)
    $("#bassMax").val(playbackSettings.bassSettings.upper)
    $("#bassStayInTessitura").val(playbackSettings.bassSettings.stayInTessitura)
    $("#bassConnectivity").val(playbackSettings.bassSettings.connectivity)
  }

  function savePlaybackSettings(){
    var songId = song.id
    var bpm = toInt($("#bpm").val())
    var repeats = toInt($("#repeats").val())
    var pianoMin = toInt($("#pianoMin").val())
    var pianoMax = toInt($("#pianoMax").val())
    var pianoStayInTessitura = toFloat($("#pianoStayInTessitura").val())
    var pianoConnectivity = toFloat($("#pianoConnectivity").val())
    var bassMin = toInt($("#bassMin").val())
    var bassMax = toInt($("#bassMax").val())
    var bassStayInTessitura = toFloat($("#bassStayInTessitura").val())
    var bassConnectivity = toFloat($("#bassConnectivity").val())

    bpm = validate(bpm, 20, 300, 120, "Tempo(BPM)")
    repeats = validate(repeats, 1, 10, 1, "Repeats")
    pianoMin = validate(pianoMin, 0, 127, 43, "Piano Min")
    pianoMax = validate(pianoMax, 0, 127, 72, "Piano Max")
    pianoStayInTessitura = validate(pianoStayInTessitura, 0.001, 10, 5, "Piano Tessitura")
    pianoConnectivity = validate(pianoConnectivity, 0.001, 10, 1, "Piano Connectivity")
    bassMin = validate(bassMin, 0, 127, 31, "Bass Min")
    bassMax = validate(bassMax, 0, 127, 55, "Bass Max")
    bassStayInTessitura = validate(bassStayInTessitura, 0.001, 10, 3, "Bass Tessitura")


    var pianoSettings = {
      lower: pianoMin,
      upper: pianoMax,
      stayInTessitura: pianoStayInTessitura,
      connectivity: pianoConnectivity
    }

    var bassSettings = {
      lower: bassMin,
      upper: bassMax,
      stayInTessitura: bassStayInTessitura,
      connectivity: bassConnectivity
    }

    var updatedPlaybackSettings = {
      songId: songId,
      bpm: bpm,
      repeats: repeats,
      pianoSettings: pianoSettings,
      bassSettings: bassSettings
    }

    playbackSettingsList[songIndex] = updatedPlaybackSettings

    loadPlaybackSettings()
  }

  function newPlaybackSettings(){

    var pianoSettings = {
      lower: 43,
      upper: 72,
      stayInTessitura: 5,
      connectivity: 1
    }

    var bassSettings = {
      lower: 31,
      upper: 55,
      stayInTessitura: 3,
      connectivity: 1
    }

    var blankPlaybackSettings = {
      songId: -1,
      bpm: 120,
      repeats: 1,
      pianoSettings: pianoSettings,
      bassSettings: bassSettings
    }

    playbackSettingsList.push(blankPlaybackSettings)


  }

  function loadPlaybackSettings(){
    playbackSettings = playbackSettingsList[songIndex]
  }






  //MIDI------------------------------------------
  $("#generateMusic").on("click", function(){

    var superSong = {
      song: song,
      playbackSettings: playbackSettings
    }

    // console.log(superSong)

    var superSongJSON = JSON.stringify(superSong)
    // console.log(superSongJSON)
    jsRoutes.controllers.JSONmaster.playbackGuestAccount(superSongJSON).ajax(
    {
      success: function(data){
        //data is the path returned
        //loadMidiString(data)

        var url = jsRoutes.controllers.JSONmaster.download(data).url
        midipath = url
        openMusicPlayer(url)

      },
      error: function(){
      },
      data: superSongJSON,
      contentType: "application/json"
    }
    )
  })

  function downloadMidi(){
    location.href = midipath
  }

  $("#downloadMIDI").on("click", function(){downloadMidi()})

  function openMusicPlayer(path){
    $("#midiplayer").hide()
    $("#downloadMIDI").hide()
    $("#midiplayer").attr("data", path)

    //wait a second to prevent clipping
    $("#midiplayer").show()
    $("#downloadMIDI").show()
  }

  //MusicXML -----------------------------------------------------------
  $("#exportMusicXML").on("click", function(){
    var songJSON = JSON.stringify(song)
    jsRoutes.controllers.JSONmaster.exportMusicXML(songJSON).ajax(
    {
      success: function(data){
        //data is the path returned
        var url = jsRoutes.controllers.JSONmaster.download(data).url
        location.href= url

      },
      error: function(){
      },
      data: songJSON,
      contentType: "application/json"
    }
    )
  })

  //Helper--------------------------------------------------------------
  function currentSongId(){
    var songId = toInt($("#songDropdown option:selected").val())
    alert(songId)
    return songId
  }

  function toInt(stringRepresentation){
    return parseInt(stringRepresentation, 10)
  }

  function toFloat(stringRepresentation){
    return parseFloat(stringRepresentation, 10)
  }
  function validate(value, min, max, defaultValue, inputName){
    if(value < min || value > max){
      alert("Invalid Value for " + inputName + ": Must be between " + min + " and " + max + ".")
      return defaultValue
    }else{
      return value
    }
  }

  //Buttons/Listeners -------------------------------------------------------------


  $("#save").on("click", function(){
    saveSong()
  })

  $('#rawText').blur(function(){//when focus leaves rawText, automatically save
    saveSong()
  });
  
  $('#currentKey').change(function(){//when focus leaves rawText, automatically save
    saveSong()
  });

  $('#destinationKey').change(function(){//when focus leaves rawText, automatically save
    saveSong()
  });

  $('#transposeOn').change(function(){//when focus leaves rawText, automatically save
    saveSong()
  });

  $('#romanNumeral').change(function(){//when focus leaves rawText, automatically save
    saveSong()
  });

  $("#songDropdown").change(function(){
    songIndex = toInt($("#songDropdown option:selected").val())
    loadSong()
  })

  $("#repeats").change(function(){
    saveSong()
  })

  $("#bpm").change(function(){
    saveSong()
  })

  $("#playbackSettingsModal :input").change(function(){
    saveSong()
  })

  $("#new").on("click", function(){
    newSong()
  })
  
  $("#delete").on("click", function(){
    deleteSong()
  })

  

})