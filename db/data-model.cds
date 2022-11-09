namespace sap.capire.taskmanager;

using {
        cuid,
        managed
} from '@sap/cds/common';

entity Roles : cuid, managed {
        name   : String(50);
        active : Boolean;
}

entity Users : cuid, managed {
        firstName : String;
        lastName  : String;
        email     : String;
        phone     : String;
        password  : String;
        active    : Boolean;
        userRole  : Association to one Roles @assert.target;
}
