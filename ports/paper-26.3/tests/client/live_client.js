const fs=require('fs');
const mc=require('@wp2508/minecraft-protocol');
const resultPath=process.argv[2];
const targetPath=process.argv[3];
const profilePackets=[];const packets={};const spawns=new Set();const metadata=new Map();let equips=0;let error;let sent=false;let ended=false;
const client=mc.createClient({host:'127.0.0.1',port:25575,username:'PortTester',auth:'offline',version:'26.3'});
function result(){
 let targets;try{targets=JSON.parse(fs.readFileSync(targetPath,'utf8'));}catch{}
 const skinReceived=targets&&profilePackets.some(p=>p.includes(targets.expectedTexture));
 const ok=!error&&sent&&skinReceived&&targets&&spawns.has(targets.player)&&spawns.has(targets.mob)&&equips>0&&metadata.size>=3;
 fs.writeFileSync(resultPath,JSON.stringify({pass:!!ok,skinReceived:!!skinReceived,error:error&&String(error),sent,targets,spawns:[...spawns],equips,metadataEntities:[...metadata.keys()],packets},null,2));
 return ok;
}
client.on('packet',(p,m)=>{
 packets[m.name]=(packets[m.name]||0)+1;
 if(m.name==='player_info')profilePackets.push(JSON.stringify(p));
 if(m.name==='spawn_entity')spawns.add(p.entityId);
 if(m.name==='entity_metadata')metadata.set(p.entityId,p.metadata);
 if(m.name==='entity_equipment')equips++;
});
client.on('position',p=>{
 console.log('POSITION',JSON.stringify(p));
 client.write('teleport_confirm',{teleportId:p.teleportId,x:p.x,y:p.y,z:p.z,yaw:p.yaw,pitch:p.pitch});
 // 26.3 teleport confirmations already carry the movement update.
});
const poll=setInterval(()=>{
 if(sent||!fs.existsSync(targetPath)||client.state!=='play')return;
 const targets=JSON.parse(fs.readFileSync(targetPath,'utf8'));
 if(!spawns.has(targets.player)||!spawns.has(targets.mob)||equips===0||!profilePackets.some(p=>p.includes(targets.expectedTexture)))return;
 sent=true;
 client.write('use_entity',{target:targets.player,hand:0,location:{x:0,y:1,z:0},sneaking:false});
 setTimeout(()=>client.write('attack',{entityId:targets.player}),1600);
},100);
client.on('error',e=>{error=e;console.error(String(e));result();});
client.on('end',reason=>{ended=true;clearInterval(poll);console.log('Client ended:',reason);const ok=result();process.exit(ok?0:1);});
setTimeout(()=>{if(!ended){error=error||'client test timeout';client.end();result();process.exit(1);}},40000);
