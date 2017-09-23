package com.lmy.core.dao ;
import org.springframework.stereotype.Component;
import com.lmy.core.model.FsFileStore;
@Component
public class FsFileStoreDao extends GenericDAOImpl<FsFileStore> {
	@Override
	public String getNameSpace() {
		return "fs_file_store";
	}
}
