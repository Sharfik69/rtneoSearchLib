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




String id = 'aa85e397-d229-d5ff-9878-7145273d60c1'

FileDescriptor fd = dataManager.load(FileDescriptor.class)
        .id(UuidProvider.fromString(id))
        .view(View.MINIMAL)
        .optional()
        .orElse(null);

InputStream inputStream = Globals.fileStorageAPI.openStream(fd);
CSVReader reader = new CSVReader(new InputStreamReader(inputStream, "UTF-8"));
data = reader.readAll();

cadastral_list = []

for (def i = 0; i < data.size; i++) {
    cadastral_list.add(data[i][0])
}

search_list = []
for (def i = 0; i < cadastral_list.size(); i++) {
    search_list.add(cadastral_list[i])
    if (search_list.size() % 1000 == 0 || i + 1 == cadastral_list.size()) {
        search(search_list);
        search_list = [];
    }
}

def json = new groovy.json.JsonBuilder()

json data: Globals.dict

return groovy.json.JsonOutput.prettyPrint(json.toString())

















def search(cad_list) {
    query = 'select e from rtneoimport$ImAPIOrders e where e.kn in (';
    for (def cadr : cad_list) {
        query += "'" + cadr + "', "
    }
    query = query.substring(0, query.length() - 2) + ")";

    List <ImAPIOrders> cadasters = dataManager.load(ImAPIOrders.class)
            .query(query)
            .view("full")
            .list();



    for (def i = 0; i < cadasters.size(); i++) {

        try {
            fd = cadasters.get(i).getFile();
            byte[] raw = Globals.fileStorageAPI.loadFile(fd);
            InputStream is = new ByteArrayInputStream(raw);
            if (is) {
                response = xmlParser(Dom4j.readDocument(is))
                response[1].remove(cadasters.get(i).getKn())
                Globals.dict[cadasters.get(i).getKn()] = response;
            }
        } catch(Exception Ignore) {}


    }
}

def xmlParser(document) {
    Element root = document.getRootElement();
    Globals.list_nodes = []
    Globals.list_owners = []
    get_all_nodes(root)
    dfs_true(root, false)
    list_owners = Globals.list_owners
    list_flats = []

    for (def i = 0; i < Globals.list_nodes.size(); i++) {
        if (Globals.list_nodes[i][0] == 'Flat') {
            try {
                kn = Globals.list_nodes[i][1].attributeValue("CadastralNumber");
                list_flats.add(kn);
            } catch(Exception Ignore) {}
        }
        try {
            kn1 = Globals.list_nodes[i][1].attributeValue("DateRemoved");
            if (kn1 != null) {
                list_owners = '!'
                break;
            }
        } catch(Exception Ignore) {}
    }
    return [list_owners, list_flats];
}

def get_all_nodes(Element root) {
    List<Element> elementsList = (List<Element>) root.elements();
    for (Element e : elementsList) {
        Globals.list_nodes.add([e.getName(), e]);
        get_all_nodes(e);
    }
}

def dfs_true(Element root, Boolean d) {
    List<Element> elementsList = (List<Element>) root.elements();
    def FIO = '';
    for (Element e : elementsList) {
        if (d) {
            FIO = FIO + e.getStringValue() + " ";
        }
        else {
            dfs_true(e, e.getName() == 'FIO');
        }
    }
    if (d) {
        Globals.list_owners.add(FIO);
    }
}
