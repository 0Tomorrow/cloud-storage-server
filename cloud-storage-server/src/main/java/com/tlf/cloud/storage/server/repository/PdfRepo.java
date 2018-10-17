package com.tlf.cloud.storage.server.repository;


import com.tlf.cloud.storage.server.entity.TPdfInfo;
import com.tlf.common.data.repository.MyRepository;

import java.util.List;

public interface PdfRepo extends MyRepository<TPdfInfo, Long> {
    List<TPdfInfo> findAllByPdfMd5OrderByPdfIndex(String pdfMd5);
}
