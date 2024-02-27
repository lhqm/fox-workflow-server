function PostMessage (){
  return this.initEventBus()
}
PostMessage.prototype = {
  constructor:PostMessage,
  initEventBus(){
    window.addEventListener("message", ({data}) => {
      if (data.action){
        this.transformEvent(data)
      }
    })
  },
  transformEvent({action,params}){
    try {
      if (this[action]){
        this[action](params)
      }
    } catch (e) {
      console.error(e)
    }
  },
  on(action,handler){
    this[action] = handler
  },
  send(iframe,params,origin="*"){
    iframe.contentWindow.postMessage(params,origin);
  }
}
export default new PostMessage()
