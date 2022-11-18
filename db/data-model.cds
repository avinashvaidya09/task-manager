namespace sap.capire.taskmanager;

using {
        cuid,
        managed
} from '@sap/cds/common';

entity Categories : managed {
        key name   : String;
            active : Boolean default TRUE;
}

entity Roles : managed {
        key name   : String(50);
            active : Boolean default TRUE;
}

entity Users : managed {
        key email     : String;
            firstName : String not null;
            lastName  : String not null;
            phone     : String not null;
            password  : String;
            active    : Boolean default TRUE;
            userRole  : Association to one Roles @assert.target;
            tasks     : Composition of many Tasks
                                on tasks.owner = $self;
            parent    : Association to one Users;
}

entity Tasks : cuid, managed {
        name          : String not null;
        description   : String(4000);
        status        : Boolean default FALSE;
        reminderCount : Integer;
        owner         : Association to one Users;
        category      : Association to one Categories @assert.target;
}
