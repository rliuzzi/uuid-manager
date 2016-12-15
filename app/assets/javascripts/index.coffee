$ ->
  $.get "/devices", (devices) ->
    $.each devices, (index, device) ->
      $("#devices").append $("<li class=\"list-group-item\">").text device.name + ": " + device.uuid

