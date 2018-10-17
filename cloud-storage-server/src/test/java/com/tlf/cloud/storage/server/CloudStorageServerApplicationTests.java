package com.tlf.cloud.storage.server;

import com.tlf.cloud.storage.server.entity.TFileInfo;
import com.tlf.cloud.storage.server.entity.TIndexInfo;
import com.tlf.cloud.storage.server.repository.FileRepo;
import com.tlf.cloud.storage.server.repository.IndexRepo;
import com.tlf.cloud.storage.server.service.FileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CloudStorageServerApplication.class)
public class CloudStorageServerApplicationTests {

	@Autowired
	FileRepo fileRepo;

	@Autowired
	IndexRepo indexRepo;

	@Autowired
	FileService fileService;

	@Test
	public void contextLoads() {
//		TIndexInfo tIndexInfo = indexRepo.getOne(502074404407083008L);
//		System.out.println(tIndexInfo.getId());
//		System.out.println(tIndexInfo);
//		TFileInfo tFileInfo = fileRepo.getOne(502074816635863040L);
//		System.out.println(tFileInfo.getId());
//		System.out.println(tFileInfo);
		System.out.println(fileService.getDownloadPath("502074816635863040"));
	}

}
