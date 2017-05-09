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
    console.log 'generate a new traceable item'

    if editor = atom.workspace.getActiveTextEditor()
      editor.insertText('- [fill-in-the-id-later]{}')

  useTemplate: (template) ->
    console.log 'Use template: ' + template
