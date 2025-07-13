package com.Infolabz.journalApp.respository;

import com.Infolabz.journalApp.entity.JournalEntry;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

//its also called mongodb respository
public interface JournalEntryRepository extends MongoRepository<JournalEntry, ObjectId> {



}

//controller (call)----> service (call)---> respository