package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.service.ImageHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("imageHistoryService")
@Transactional
public class DefaultImageHistoryService implements ImageHistoryService {
}
