package Upload;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import CitSciClasses.FormAttribute;
import CitSciClasses.SiteCharecteristics;
import com.nrel.citsci.DatasheetMainActivity;


/**
 * Created by Manoj on 3/23/2015.
 * Imported from code by Pramod for previous version
 */
public class XMLPreparer {

    public static final String XML_ROOT_DIR = "/CitsciMobile/";
    private static final String PROJECT_EXTRA = "Project";

    private static DataSheetObservation dataSheetObservation;
    public static File xmlFile;
    String fileName;
    int fid = 0;
    static Context datasheetContext;
    public XMLPreparer(DataSheetObservation dataSheetObservation, Context datasheetContext){
        this.dataSheetObservation=dataSheetObservation;
        this.datasheetContext = datasheetContext;
    }

    public void createXML() throws SAXException, IOException {
        try {
            File rootDir = new File(Environment.getExternalStorageDirectory() + XML_ROOT_DIR);
            //Log.d("XMLPreparer: ", rootDir.getAbsolutePath()+" Here");
            if (!rootDir.exists()) {
                rootDir.mkdirs();
            }
            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            fileName = URLEncoder.encode(dataSheetObservation.getObservationName().replaceAll("[^A-Za-z0-9]", "")+"_"+dataSheetObservation.getDatasaheetId(),"utf-8");


            xmlFile = new File(Environment.getExternalStorageDirectory() + XML_ROOT_DIR + fileName + "_" + fid  + ".xml");
            while(xmlFile.exists()){
                fid++;
                xmlFile = new File(Environment.getExternalStorageDirectory() + XML_ROOT_DIR + fileName + "_" + fid  + ".xml");
            }
            //Log.d("XMLPreparer: ", "Filename " + xmlFile.getAbsolutePath());;
            xmlFile.createNewFile();
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element root = doc.createElement("GODMData");
            doc.appendChild(root);
            root.setAttribute("FormId", dataSheetObservation.getDatasaheetId());
            Element project = doc.createElement(PROJECT_EXTRA);
            project.setAttribute("ID", dataSheetObservation.getProjectId());

            // Add ObservationForm1(Location Details) Data
            Element name = doc.createElement("Name");
            name.setAttribute("Value", dataSheetObservation.getDatasheetName());
            project.appendChild(name);

            // Add ObservationForm2 (Date Details) Data.
            Element visit = doc.createElement("Visit");
            visit.setAttribute("Date", dataSheetObservation.getDate());
            Element Authority = doc.createElement("Authority");
            Element AuthorityOption = doc.createElement("AuthorityOption");
            AuthorityOption.setAttribute("Value", dataSheetObservation.getAuthority());
            AuthorityOption.setAttribute("ID", dataSheetObservation.getAuthorityId());
            Authority.appendChild(AuthorityOption);
            visit.appendChild(Authority);
            Element Recorder = doc.createElement("Recorder");
            Element RecorderOption = doc.createElement("RecorderOption");
            RecorderOption.setAttribute("Value", dataSheetObservation.getRecorder());
            //RecorderOption.setAttribute("Value", "manoj");
            Recorder.appendChild(RecorderOption);
            visit.appendChild(Recorder);
            Element Time = doc.createElement("Time");
            Time.setAttribute("Value", dataSheetObservation.getTime());
            visit.appendChild(Time);
            Element VisitComment = doc.createElement("VisitComment");
            VisitComment.setAttribute("Value", dataSheetObservation.getComments());
            visit.appendChild(VisitComment);

            // Add Organism Info Details.
            ArrayList<OrganismObservation> organismsDataList = dataSheetObservation.getOrganismObservations(datasheetContext);
            for (int i = 0; i < organismsDataList.size(); i++) {

                ArrayList<FormAttribute> attributeList = organismsDataList.get(i).getAttributes();
                Element OrganismData = doc.createElement("OrganismData");

                if (organismsDataList.get(i).getName() != null && organismsDataList.get(i).getName().trim().length() > 0) {
                    Element OrganismDataOptionName = doc.createElement("OrganismDataOption");
                    OrganismDataOptionName.setAttribute("OrganismName", organismsDataList.get(i).getName());
                    OrganismData.appendChild(OrganismDataOptionName);
                }

                if (organismsDataList.get(i).getId() != null && organismsDataList.get(i).getId().trim().length() > 0) {
                    Element OrganismDataOptionId = doc.createElement("OrganismDataOption");
                    OrganismDataOptionId.setAttribute("OrganismInfoID", organismsDataList.get(i).getId());

                    OrganismData.appendChild(OrganismDataOptionId);
                }

                if (organismsDataList.get(i).getComment() != null && organismsDataList.get(i).getComment().trim().length() > 0) {
                    Element OrganismDataOptionId = doc.createElement("OrganismDataOption");
                    OrganismDataOptionId.setAttribute("OrganismComment", organismsDataList.get(i).getComment());
                    OrganismData.appendChild(OrganismDataOptionId);
                }

                Element AttributeData = doc.createElement("AttributeData");

                for (int j = 0; j < attributeList.size(); j++) {
                    if (attributeList.get(j).getSelectedValue() != null && attributeList.get(j).getSelectedValue().trim().length() > 0) {
                        Element AttributeDataOption = doc.createElement("AttributeDataOption");
                        AttributeDataOption.setAttribute("Value", attributeList.get(j).getSelectedValue());
                        AttributeDataOption.setAttribute("UnitID", attributeList.get(j).getUnitID());
                        AttributeDataOption.setAttribute("AttributeDataTypeID", attributeList.get(j).getId());
                        AttributeData.appendChild(AttributeDataOption);
                    }
                }
                // Add organism only if attribute data exists.
                if (AttributeData.getChildNodes().getLength() > 0) {
                    visit.appendChild(OrganismData);
                    OrganismData.appendChild(AttributeData);
                } else if (AttributeData.getChildNodes().getLength() == 0) {
                    visit.appendChild(OrganismData);
                }

            }

            // Add SiteCharacteristics.
            ArrayList<SiteCharecteristics> siteCharateristicsData = dataSheetObservation.getSiteCharecteristics();
            if (siteCharateristicsData != null) {
                Element SiteCharacteristics = doc.createElement("SiteCharacteristics");
                for (int j = 0; j < siteCharateristicsData.size(); j++) {
                    if (siteCharateristicsData.get(j).getSelectedValue() != null && siteCharateristicsData.get(j).getSelectedValue().trim().length() > 0) {
                        Element AttributeDataOption = doc.createElement("AttributeDataOption");
                        AttributeDataOption.setAttribute("Value", siteCharateristicsData.get(j).getSelectedValue());
                        AttributeDataOption.setAttribute("UnitID", siteCharateristicsData.get(j).getUnitID());
                        AttributeDataOption.setAttribute("AttributeDataTypeID", siteCharateristicsData.get(j).getId());
                        SiteCharacteristics.appendChild(AttributeDataOption);
                    }
                }
                if (SiteCharacteristics.getChildNodes().getLength() > 0) {
                    visit.appendChild(SiteCharacteristics);
                } else if (SiteCharacteristics.getChildNodes().getLength() == 0) {

                }
            }

            Element area = doc.createElement("Area");
            area.setAttribute("AreaName", dataSheetObservation.getObservationName());
            area.setAttribute("AreaID", dataSheetObservation.getAreaID());
            area.setAttribute("X", dataSheetObservation.getLongitude());
            area.setAttribute("Y", dataSheetObservation.getLatitude());
            area.setAttribute("CoordinateSystemID", "1");
            area.setAttribute("Accuracy", dataSheetObservation.getAccuracy());
            area.appendChild(visit);
            project.appendChild(area);


            root.appendChild(project);

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);
            //new PostDataTaskHandler().execute();
            //postData(organism, organismsDataList);
            saveImagesFromTempFolder();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    private void saveImagesFromTempFolder() {
        String imageTempPath = DatasheetMainActivity.imageTempPath;
        File imageDir = new File(Environment.getExternalStorageDirectory() + XML_ROOT_DIR + fileName+"_"+fid+"/");
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
        File dir = new File(Environment.getExternalStorageDirectory()+imageTempPath);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                try {
                    copyFile(new File(dir, children[i]),new File(Environment.getExternalStorageDirectory() + XML_ROOT_DIR + fileName+"_"+fid+"/"+children[i]));
                    new File(dir, children[i]).delete();
                } catch (IOException e) {
                    Toast.makeText(datasheetContext, "Image was not saved due to an exception. You will still  be able to recover this file from the temp folder before you start your next observation ", Toast.LENGTH_LONG).show();
                }

            }
        }
        DatasheetMainActivity.clearTempImages(datasheetContext);
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }


    }
}
