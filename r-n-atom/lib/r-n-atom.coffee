RNAtomView = require './r-n-atom-view'
{CompositeDisposable} = require 'atom'

module.exports = RNAtom =
  rNAtomView: null
  modalPanel: null
  subscriptions: null

  activate: (state) ->
    @rNAtomView = new RNAtomView(state.rNAtomViewState)
    @modalPanel = atom.workspace.addModalPanel(item: @rNAtomView.getElement(), visible: false)

    # Events subscribed to in atom's system can be easily cleaned up with a CompositeDisposable
    @subscriptions = new CompositeDisposable

    # Register command that toggles this view
    @subscriptions.add atom.commands.add 'atom-workspace', 'r-n-atom:toggle': => @toggle()
    @subscriptions.add atom.commands.add 'atom-workspace', 'r-n-atom:generate-traceable-item': => @generateTraceableItem()
    @subscriptions.add atom.commands.add 'atom-workspace', 'r-n-atom:use-template-strs': => @useTemplate('StRS')

  deactivate: ->
    @modalPanel.destroy()
    @subscriptions.dispose()
    @rNAtomView.destroy()

  serialize: ->
    rNAtomViewState: @rNAtomView.serialize()

  toggle: ->
    console.log 'RNAtom was toggled!'

    if @modalPanel.isVisible()
      @modalPanel.hide()
    else
      @modalPanel.show()

  generateTraceableItem: ->
    console.log 'Generate a new traceable item'

    if editor = atom.workspace.getActiveTextEditor()
      filenameTag = editor?.buffer.file?.getBaseName().split('.')[0]

      lines = editor.getText().split '\n'
      itemIds = []
      for line,i in lines
        if match = /^\t*-\s\[(.*)\]\{(.*)\}/i.exec line
          item = match[1]
          itemId = item.split(filenameTag + '-')[1]
          if itemId?   #If itemId is not "undefined"
            itemIds.push +itemId   #The plus sign convents string to int

      newItemId = 0
      if itemIds.length != 0
        newItemId = itemIds.reduce (a,b) -> Math.max a, b   #ES5 reduce method to calculate the max of an array
      ++newItemId

      editor.insertText('- [' + filenameTag + '-' + '0000'.substr(0, 4-(''+newItemId).length) + newItemId + ']{}')

  useTemplate: (template) ->
    console.log 'Use template: ' + template
