package com.Infolabz.journalApp.respository;

import com.Infolabz.journalApp.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String>
{



    User findByuserName(String username);

    User deleteUserByUserName(String username);

}

//handles the actual database operation. It converts the User object into a MongoDB document.
