$(document).ready(function(){
	$( "#eq > span" ).each(function() {
      // read initial vals from markup and remove that
      var val = toInt( $( this ).text());
      $( this ).empty().slider({
        val: val,
        range: "min",
        animate: true,
        orientation: "vertical"
      });
    });


  var GUEST_USER_ID = 1
  var userIdPrecursor = toInt($("#userId").attr("data"))
  var userId = function(){if(isNaN(userIdPrecursor)){return GUEST_USER_ID}else{return userIdPrecursor}}()

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


  var songList

  initialize()

  function initialize(){
    getSongs(function(){
      var firstSongId = toInt($("#songDropdown").first().val())
      getSong(firstSongId)
    })
  }


  function getSong(id){
    jsRoutes.controllers.JSONmaster.getSong(id).ajax({
      success: function(data){
        song = data
        console.log(song)

        updateSongFieldDisplay()
        printFormattedText(song)
        getPlaybackSettings(id)
      },
      error: function(err){
        alert("Error opening song")
      }
    })
  }

  $("#new").on("click", function(){
    newSong()
  })

  function newSong(){
    jsRoutes.controllers.JSONmaster.newSong(userId).ajax({
      success: function(data){
        var newSongId = toInt(data)

        getSongs(function(){
          getSong(newSongId)
          $('#songDropdown').val(newSongId)
        })

        updateSongFieldDisplay()
        printFormattedText(song)
        getPlaybackSettings(newSongId)
      },
      error: function(err){
        alert("Error opening song")
      }
    })
  }

  function getPlaybackSettings(id){
    jsRoutes.controllers.JSONmaster.getPlaybackSettings(id).ajax({
      success: function(data){
        playbackSettings = data
        console.log(playbackSettings)
      },
      error: function(err){
        alert("Error opening playbackSettings")
      }
    })
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

  function updatePlaybackSettings(){
    $("#bpm").val(playbackSettings.bpm)
    $("#repeats").val(playbackSettings.repeats)
    $("#composer").val(song.composer)
    $("#dateCreated").val(song.dateCreated)
    $("#timeSig").val(song.timeSig)
    $("#currentKey").val(song.currentKey)
    $("#destinationKey").val(song.destinationKey)
    $("#transposeOn").attr('checked', song.transposeOn);
    $("#romanNumeral").attr('checked', song.romanNumeral);
    $("#rawText").val(song.rawText)
  }

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
      userId: GUEST_USER_ID};

    song = newSong

    var songJSON = JSON.stringify(newSong)


    console.log(newSong)
    jsRoutes.controllers.JSONmaster.saveSong(songJSON).ajax(
    {
      success: function(data){
        updateSongFieldDisplay()
        printFormattedText(song)
      },
      error: function(){
      },
      data: songJSON,
      contentType: "application/json"
    }
    )
  }

  function printFormattedText(song){
    var songJSON = JSON.stringify(song)
    jsRoutes.controllers.JSONmaster.formatText(songJSON).ajax(
    {
      success: function(data){
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
  }

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

  $("#generateMusic").on("click", function(){
    var songJSON = JSON.stringify(song)
    jsRoutes.controllers.JSONmaster.playback(songJSON).ajax(
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

  function populateSongDropdown(){
    $("#songDropdown").empty()//clear first

    for (var i = 0; i < songList.length; i++){

      $("#songDropdown").append(
        $('<option>').text(songList[i].label).val(songList[i].id)
      )
    }
  }

  $("#songDropdown").change(function(){

    getSong(toInt($("#songDropdown option:selected").val()))
  })

  function currentSongId(){
    var songId = toInt($("#songDropdown option:selected").val())
    alert(songId)
    return songId
  }

  function toInt(stringRepresentation){
    return parseInt(stringRepresentation, 10)
  }



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