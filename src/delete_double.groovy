///

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




String id = '75977de9-5074-d1ea-15ea-2c79e11149f4'

FileDescriptor fd = dataManager.load(FileDescriptor.class)
        .id(UuidProvider.fromString(id))
        .view(View.MINIMAL)
        .optional()
        .orElse(null);

InputStream inputStream = Globals.fileStorageAPI.openStream(fd);
CSVReader reader = new CSVReader(new InputStreamReader(inputStream, "UTF-8"));
data = reader.readAll();

cadastral_list = []
def sze = 20000

for (def i = 15000; i < sze; i++) {
    cadastral_list.add(data[i][0])
}

def start_from = 0 //ЭТО УЖЕ БЫЛО
def to_ = cadastral_list.size()

for (def i = start_from; i < to_; i++) {

    List <SyncAccountIPK> l = dataManager.load(SyncAccountIPK.class)
            .query('select e from rtneoimport$SyncAccountIPK e where e.cadastralNumber = :param')
            .parameter("param", cadastral_list[i])
            .list();

    def cbv = [];

    for (def j = 0; j < l.size(); j++) {
        SyncAccountIPK x = l.get(j);
        if (x.getAccountNumber() != null) {
            cbv.add(x)
        }
    }

    for(def j = 0; j < cbv.size(); j++) {
        l.remove(cbv.get(j))
    }

    for (def j = 0; j < l.size() - 1; j++) {
        dataManager.remove(l.get(j));
    }


}
