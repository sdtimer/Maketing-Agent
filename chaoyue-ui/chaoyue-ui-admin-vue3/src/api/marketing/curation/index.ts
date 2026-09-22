import request from '@/config/axios'
export interface CurationBatch { id:number; market:string; channel:string; opsNote?:string; status:string; publishedAt?:string; createTime:string }
export const getCurationBatches=()=>request.get<CurationBatch[]>({url:'/marketing/curation/batch/list'})
export const createCurationBatch=(data:{market:string;channel:string;opsNote?:string})=>request.post<number>({url:'/marketing/curation/batch',data})
export const addCurationEntry=(id:number,data:{taskId:number;sortOrder?:number;recommendText?:string;reason?:string})=>request.post<boolean>({url:`/marketing/curation/batch/${id}/entries`,data})
export const publishCurationBatch=(id:number)=>request.post<boolean>({url:`/marketing/curation/batch/${id}/publish`})
export const offlineCurationBatch=(id:number)=>request.post<boolean>({url:`/marketing/curation/batch/${id}/offline`})
