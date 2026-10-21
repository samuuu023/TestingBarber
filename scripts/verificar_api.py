"""Humo/aceptación HTTP real; iniciar la API antes de ejecutarlo.
Crea un servicio con nombre único y lo elimina al terminar.
"""
import urllib.request,urllib.error,json,uuid,argparse
from pathlib import Path

def run(base):
 cases=[]
 def request(method,path,data=None,token=None,expected=200):
  headers={'Content-Type':'application/json'}
  if token:headers['Authorization']='Bearer '+token
  req=urllib.request.Request(base+path,method=method,headers=headers,data=None if data is None else json.dumps(data).encode())
  try:
   with urllib.request.urlopen(req,timeout=20) as response:status=response.status;raw=response.read()
  except urllib.error.HTTPError as e:status=e.code;raw=e.read()
  cases.append({'paso':method+' '+path,'esperado':expected,'obtenido':status,'aprobado':status==expected})
  if status!=expected:raise AssertionError(cases[-1])
  return json.loads(raw) if raw else None
 request('GET','/api/salud')
 request('GET','/api/servicios',expected=401)
 token=request('POST','/api/auth/login',{'email':'admin@barberia.com','password':'Admin123!'})['accessToken']
 request('GET','/api/auth/me',token=token)
 body={'nombre':'Prueba HTTP '+uuid.uuid4().hex[:10],'precio':6,'duracionMinutos':30}
 s=request('POST','/api/servicios',body,token,201);id=s['id']
 try:
  request('GET',f'/api/servicios/{id}',token=token)
  records=request('GET','/api/servicios',token=token);assert any(x['id']==id for x in records)
  body['precio']=8.5;updated=request('PUT',f'/api/servicios/{id}',body,token);assert updated['precio']==8.5
  request('POST','/api/servicios',body,token,409)
 finally:request('DELETE',f'/api/servicios/{id}',token=token,expected=204)
 request('GET',f'/api/servicios/{id}',token=token,expected=404)
 return cases
if __name__=='__main__':
 p=argparse.ArgumentParser();p.add_argument('--base',default='http://localhost:8080');p.add_argument('--output',default='resultado_http.json');a=p.parse_args()
 result=run(a.base.rstrip('/'));Path(a.output).write_text(json.dumps(result,indent=2),encoding='utf-8');print(f'{len(result)} pasos HTTP aprobados')
