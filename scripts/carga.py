"""Carga local limitada. Iniciar API; ejecutar: python scripts/carga.py --requests 100 --workers 5.
No usar contra servidores de terceros sin autorización. Solo librería estándar de Python 3.
"""
import argparse,concurrent.futures,json,time,urllib.request,urllib.error,statistics
from pathlib import Path

def run(base,total,workers):
 req=urllib.request.Request(base+'/api/auth/login',data=json.dumps({'email':'admin@barberia.com','password':'Admin123!'}).encode(),headers={'Content-Type':'application/json'})
 with urllib.request.urlopen(req,timeout=15) as response:token=json.load(response)['accessToken']
 def one(_):
  start=time.perf_counter()
  try:
   req=urllib.request.Request(base+'/api/servicios',headers={'Authorization':'Bearer '+token})
   with urllib.request.urlopen(req,timeout=15) as response:response.read();status=response.status
  except urllib.error.HTTPError as e:status=e.code
  except Exception:status=0
  return status,(time.perf_counter()-start)*1000
 start=time.perf_counter()
 with concurrent.futures.ThreadPoolExecutor(max_workers=workers) as pool:results=list(pool.map(one,range(total)))
 elapsed=time.perf_counter()-start;times=sorted(t for _,t in results)
 report={'tipo':'carga local limitada GET /api/servicios','requests':total,'workers':workers,'ok':sum(s==200 for s,_ in results),'errores':sum(s!=200 for s,_ in results),'duracion_s':round(elapsed,3),'rps':round(total/elapsed,2),'media_ms':round(statistics.mean(times),2),'p95_ms':round(times[max(0,__import__('math').ceil(len(times)*.95)-1)],2),'limite':'No es una prueba de estrés, producción ni carga sostenida.'}
 return report
if __name__=='__main__':
 p=argparse.ArgumentParser();p.add_argument('--base',default='http://localhost:8080');p.add_argument('--requests',type=int,default=100);p.add_argument('--workers',type=int,default=5);p.add_argument('--output',default='resultado_carga.json');a=p.parse_args()
 if not 1<=a.requests<=10000 or not 1<=a.workers<=50:p.error('requests 1..10000 y workers 1..50')
 report=run(a.base.rstrip('/'),a.requests,a.workers);Path(a.output).write_text(json.dumps(report,indent=2),encoding='utf-8');print(json.dumps(report,indent=2));raise SystemExit(report['errores']!=0)
