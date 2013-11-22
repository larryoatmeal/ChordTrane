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
  var userIdPrecursor 
  var userId

  //Song
  var song 
  //part of song
  // var title
  // var composer
  // var dateCreated
  // var currentKey
  // var destinationKey
  // var transposeOn
  // var romanNumeral
  // var rawText

  //Playback Settings
  var playbackSettings
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

  var songList//label and id

  //MIDI
  var player

  var midipath

  var charactersInLongestLine
  var renderedText

  initialize()
  //initializeMidiJs()

  function initialize(){
    userIdPrecursor = toInt($("#userId").attr("data"))
    userId = function(){if(isNaN(userIdPrecursor)){return GUEST_USER_ID}else{return userIdPrecursor}}()

    getSongs(function(){
      var firstSongId = toInt($("#songDropdown").first().val())
      getSong(firstSongId)
    })
  }

  // SONG CRUD ------------------------------------------------------------------------------
  function getSong(id){
    jsRoutes.controllers.JSONmaster.getSong(id).ajax({
      success: function(data){
        song = data
        console.log(song)

        loadSong()
      },
      error: function(err){
        alert("Error opening song")
      }
    })
  }

  function getSongs(loadFunction){
    //if no user (on guest account)
    jsRoutes.controllers.JSONmaster.getSongs(userId).ajax(
      {
        success: function(data){
          songList = data
          populateSongDropdown()
          console.log(data)
          loadFunction()
        },
        error: function(){
        }
      }
    )
  }

  function newSong(){
    jsRoutes.controllers.JSONmaster.newSong(userId).ajax({
      success: function(data){
        var newSongId = toInt(data)
        getSongs(function(){//Reget songs and set current song to new song
          getSong(newSongId)
          $('#songDropdown').val(newSongId)
        })

        loadSong()
      },
      error: function(err){
        alert("Error opening song")
      }
    })
  }

  function deleteSong(){
    jsRoutes.controllers.JSONmaster.deleteSong(song.id).ajax({
      success: function(data){

        getSongs(function(){
          getSong(toInt($("#songDropdown").first().val()))//get first song in dropdown
        })

        loadSong()
      },
      error: function(err){
        alert("Error deleting song")
      }
    })
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

    var newSong={
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
      userId: userId};

    song = newSong

    var songJSON = JSON.stringify(newSong)


    console.log(newSong)
    jsRoutes.controllers.JSONmaster.saveSong(songJSON).ajax(
    {
      success: function(data){
        loadSong()
        //Change text of dropdown
        $("#songDropdown option:selected").text(title)
      },
      error: function(){
      },
      data: songJSON,
      contentType: "application/json"
    }
    )

    savePlaybackSettings()//when ever song saved, save playback settings
  }

  

  //SONG Rendering/Display ---------------------------------------------------------------

  function loadSong(){
    updateSongFieldDisplay()
    printFormattedText()
    getPlaybackSettings(song.id, updatePlaybackSettingsFieldDisplay)
  }



  function populateSongDropdown(){
    $("#songDropdown").empty()//clear first

    for (var i = 0; i < songList.length; i++){

      $("#songDropdown").append(
        $('<option>').text(songList[i].label).val(songList[i].id)
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
    $("#transposeOn").prop('checked', song.transposeOn);
    $("#romanNumeral").prop('checked', song.romanNumeral);
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
        console.log(formatted)
      
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
  function getPlaybackSettings(id, loadFunction){
    jsRoutes.controllers.JSONmaster.getPlaybackSettings(id).ajax({
      success: function(data){
        playbackSettings = data
        console.log(playbackSettings)
        loadFunction()//must change display after callback
      },
      error: function(err){
        console.log("Error opening playbackSettings")
      }
    })
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

    var playbackSettingsJSON = JSON.stringify(updatedPlaybackSettings)
    jsRoutes.controllers.JSONmaster.savePlaybackSettings(playbackSettingsJSON).ajax({
      success: function(data){

      },
      error: function(err){
        console.log("Error saving playbackSettings")
      },
      data: playbackSettingsJSON,
      contentType: "application/json"
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


  //MIDI------------------------------------------
  $("#generateMusic").on("click", function(){
    var songJSON = JSON.stringify(song)
    jsRoutes.controllers.JSONmaster.playback(songJSON).ajax(
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
      data: songJSON,
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

    $("#downloadMIDI").show()
    $("#midiplayer").attr("data", path)

    //wait a second to prevent clipping
    $("#midiplayer").show()
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

  $("#repeats").change(function(){
    saveSong()
  })

  $("#bpm").change(function(){
    saveSong()
  })

  $("#playbackSettingsModal :input").change(function(){
    saveSong()
  })

  $("#songDropdown").change(function(){
    getSong(toInt($("#songDropdown option:selected").val()))
  })
  
  $("#new").on("click", function(){
    newSong()
  })

  $("#delete").on("click", function(){
    deleteSong()
  })





  

  

  

  // function loadMidiString(path){

  //   var url = jsRoutes.controllers.JSONmaster.midiAsBase64(path).url

  //   $.get(url, function(data){
  //     console.log(data)

  //     //player.loadFile(data, player.start)
  //   })
  //   // $.ajax({
  //   //   type:'GET',
  //   //   url: url,
  //   //   data: {},
  //   //   success: function(data) {
  //   //    console.log(data); 
  //   //   }, error: function(err) {
  //   //    console.log("Error"); 
  //   //   }
  //   // });
  // }

  // function initializeMidiJs(){
  //   MIDI.loadPlugin({
  //     soundfontUrl: "/assets/soundfonts/",
  //     instruments: ["acoustic_grand_piano", "acoustic_bass", "synth_drum"],
  //     callback: function(){
  //       player = MIDI.Player
  //       player.timeWarp = 1
  //     }
  //   })
  // }



  

  

  

  


  // function resize(){
  //   var widthDisplay = $("#renderedText").width()
  //   // var defaultFontSize = toInt($("#renderedText").css("font-size"))
  //   var startingFont = 15
  //   var font = startingFont
  //   // alert(width)
  //   // var startingWidthOfCharacters = font * charactersInLongestLine

  //   var test = document.getElementById("FontTest")
  //   document.getElementById("FontTest").innerHTML = renderedText

  //   $("#FontTest").css("font-size", startingFont)
  //   var widthOfText = $("#FontTest").width() + 1


  //   alert(widthOfText)
  //   if(widthOfText > widthDisplay){//too big, scale down
  //     alert("Too big")
  //     while(widthOfText > widthDisplay){
  //       font--
  //       alert(font)
  //       $("#FontTest").css("font-size", font)
  //       widthOfText = $("#FontTest").width() + 1
  //       alert(widthOfText)
  //     }
  //   }

  //   alert(font)
  //   $("#renderedText").css("font-size", font)
  // }

  // $(window).on('resize', function(){
  //   resize()
  // })

  // function saveSong(){
  //   var title = $("#title").val()
  //   var composer = $("#composer").val()
  //   var dateCreated = $("#dateCreated").val()
  //   var timeSig = $("#timeSig").val()
  //   var currentKey = $("#currentKey").val()
  //   var destinationKey = $("#destinationKey").val()
  //   var transposeOn = $("#transposeOn").is(':checked')
  //   var romanNumeral = $("#romanNumeral").is(':checked')
  //   var rawText = $("#rawText").val()

  //   newSong={
  //     id: song.id,
  //     rawText: rawText,
  //     title: title, 
  //     composer: composer, 
  //     dateCreated: dateCreated, 
  //     currentKey: currentKey, 
  //     destinationKey: destinationKey,
  //     transposeOn: transposeOn,
  //     romanNumeral: romanNumeral,
  //     userId: GUEST_USER_ID};
  //   songJSON = JSON.stringify(newSong)
  //   console.log(newSong)
  //   jsRoutes.controllers.JSONmaster.saveSong(songJSON).ajax(
  //   {
  //     success: function(data){
  //     },
  //     error: function(){
  //     },
  //     data: songJSON,
  //     contentType: "application/json"
  //   }
  //   )
  // }	
})