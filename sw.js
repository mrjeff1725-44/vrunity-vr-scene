const PREFIX='vrunity-'+self.registration.scope;
const C=PREFIX+"vr-1790819259190";
const INDEX=new URL('./index.html',self.registration.scope).href;
const FILES=['./','./index.html','./manifest.webmanifest','./icon.svg','./three.module.min.js','./three.core.min.js'];
self.addEventListener('install',event=>{
  self.skipWaiting();
  event.waitUntil(caches.open(C).then(async cache=>{
    await Promise.all(FILES.map(async file=>{try{await cache.add(file)}catch{}}));
  }));
});
self.addEventListener('activate',event=>{
  event.waitUntil((async()=>{
    const keys=await caches.keys();
    await Promise.all(keys.filter(key=>key.startsWith(PREFIX)&&key!==C).map(key=>caches.delete(key)));
    await self.clients.claim();
  })());
});
self.addEventListener('fetch',event=>{
  const url=new URL(event.request.url);
  if(event.request.method!=='GET'||url.origin!==self.location.origin||!url.pathname.startsWith(new URL(self.registration.scope).pathname))return;
  event.respondWith((async()=>{
    const cache=await caches.open(C);
    try{
      const response=await fetch(event.request);
      if(response.ok){await cache.put(event.request,response.clone());if(event.request.mode==='navigate')await cache.put(INDEX,response.clone())}
      return response;
    }catch(error){
      const cached=await cache.match(event.request);
      if(cached)return cached;
      if(event.request.mode==='navigate'){const page=await cache.match(INDEX);if(page)return page}
      throw error;
    }
  })());
});