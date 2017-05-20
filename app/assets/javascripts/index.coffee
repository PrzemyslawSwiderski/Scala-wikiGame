jQuery ($) ->
  $.getJSON "/api/players", (players) ->
    $.each players, (index, player) ->
      nickName = $("<div>").addClass("nickName").text player.nickName
      score = $("<div>").addClass("score").text player.score
      nickNameLabel = $("<label>").addClass("nickNameLabel").text "Nick: "
      scoreLabel = $("<label>").addClass("scoreLabel").text "Score: "
      $("#players").append $("<li>").append(nickNameLabel).append(nickName).append(scoreLabel).append(score)