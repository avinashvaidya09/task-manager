namespace sap.capire.taskmanager;

using {
        cuid,
        managed
} from '@sap/cds/common';

entity Categories : cuid, managed {
        category : String not null;
        active   : Boolean default TRUE;
}

entity Roles : cuid, managed {
        name   : String(50) not null;
        active : Boolean default TRUE;
}

entity Users : cuid, managed {
        firstName : String not null;
        lastName  : String not null;
        email     : String not null;
        phone     : String not null;
        password  : String not null;
        active    : Boolean default TRUE;
        userRole  : Association to one Roles not null @assert.target;
        tasks     : Association to many Tasks
                            on tasks.owner = $self;
        parent: Association to one Users;
        children: Composition of many Users on children.parent = $self;
}

entity Tasks : cuid, managed {
        name          : String not null;
        status        : Boolean default FALSE;
        reminderCount : Integer;
        owner         : Association to one Users;
        category      : Association to one Categories @assert.target;
}
