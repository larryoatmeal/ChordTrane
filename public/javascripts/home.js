$(document).ready(function(){
	$( "#eq > span" ).each(function() {
      // read initial vals from markup and remove that
      var val = parseInt( $( this ).text(), 10 );
      $( this ).empty().slider({
        val: val,
        range: "min",
        animate: true,
        orientation: "vertical"
      });
    });


  var GUEST_USER_ID = 1

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

  getSong(1)

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

  $("#save").on("click", function(){
    saveSong()
  })



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
      error: function(){
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