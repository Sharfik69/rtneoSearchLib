//
import java.util.*;
import com.groupstp.rtneo.entity.*;
import com.groupstp.rtneoimport.entity.*;
import com.opencsv.CSVReader;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.app.FileStorageAPI;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayOutputStream;
import org.apache.commons.io.IOUtils;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*
import com.haulmont.cuba.core.global.*
import com.groupstp.rtneo.entity.*
import com.haulmont.bali.util.*;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.View;
import com.haulmont.chile.core.model.MetaClass
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;

class Globals {
    static list_nodes = [];
    static list_owners = [];
    static DataManager dataManager = AppBeans.get(DataManager.NAME)
    static FileStorageAPI fileStorageAPI = AppBeans.get(FileStorageAPI.NAME)
    static dict = [:]
}




String id = '732db6b7-020c-35de-67cd-49e616c33f22'

FileDescriptor fd = dataManager.load(FileDescriptor.class)
        .id(UuidProvider.fromString(id))
        .view(View.MINIMAL)
        .optional()
        .orElse(null);

InputStream inputStream = Globals.fileStorageAPI.openStream(fd);
CSVReader reader = new CSVReader(new InputStreamReader(inputStream, "UTF-8"));
data = reader.readAll();

cadastral_list = []
def sze = data.size
sze = 10
for (def i = 0; i < sze; i++) {
    cadastral_list.add(data[i][0])
}

SyncAccountIPK a = new SyncAccountIPK()
a.setCadastralNumber(cadastral_list[0])
dataManager.commit(a)