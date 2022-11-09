using { sap.capire.taskmanager as db  } from '../db/data-model';

service AdminService {
    
    entity Role as projection on db.Roles;
    entity User as projection on db.Users;

}