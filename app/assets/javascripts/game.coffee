wikiPage = $('#wikiPage')
$(document).ready ->
  $('#start_btn').click ->
    page = $('#fsq').val()
    mwjs = new MediaWikiJS('https://pl.wikipedia.org', {
      action: 'parse'
      page: page
    }, (data) ->
      wikiPage.html data.parse.text['*']
      wikiPage.find('a').removeAttr 'href'
      return
    )
    return
  return
setInterval (->
  wikiPage.find('a').on 'click', ->
    title = $(this).attr('title')
    mwjs = new MediaWikiJS('https://pl.wikipedia.org', {
      action: 'parse'
      page: title
    }, (data) ->
      wikiPage.html data.parse.text['*']
      wikiPage.find('a').removeAttr 'href'
      return
    )
    return
  return
), 1000
