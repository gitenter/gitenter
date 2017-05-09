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
