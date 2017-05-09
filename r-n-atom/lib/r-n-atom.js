'use babel';

import RNAtomView from './r-n-atom-view';
import { CompositeDisposable } from 'atom';

export default {

  rNAtomView: null,
  modalPanel: null,
  subscriptions: null,

  activate(state) {
    this.rNAtomView = new RNAtomView(state.rNAtomViewState);
    this.modalPanel = atom.workspace.addModalPanel({
      item: this.rNAtomView.getElement(),
      visible: false
    });

    // Events subscribed to in atom's system can be easily cleaned up with a CompositeDisposable
    this.subscriptions = new CompositeDisposable();

    // Register command that toggles this view
    this.subscriptions.add(atom.commands.add('atom-workspace', {
      'r-n-atom:toggle': () => this.toggle()
    }));
  },

  deactivate() {
    this.modalPanel.destroy();
    this.subscriptions.dispose();
    this.rNAtomView.destroy();
  },

  serialize() {
    return {
      rNAtomViewState: this.rNAtomView.serialize()
    };
  },

  toggle() {
    console.log('RNAtom was toggled!');
    return (
      this.modalPanel.isVisible() ?
      this.modalPanel.hide() :
      this.modalPanel.show()
    );
  }

};
