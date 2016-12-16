$ ->

  $.get getPath(), (devices) ->
    $.each devices, (index, device) ->
      $("#devices").append $("<li class=\"list-group-item\">").text device.name + ": " + device.uuid

getContext = ->
  path = window.location.pathname
  context = path.split("/")
  context[1]

getPath = ->

  if !!getContext()
    context = getContext()
  else
    context = "zone"
  path = context + "/devices"
  path

console.log(getPath())
