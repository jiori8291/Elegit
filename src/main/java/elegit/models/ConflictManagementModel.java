package elegit.models;

import elegit.Main;
import elegit.exceptions.ExceptionAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


/**
 * Created by grenche on 6/19/18.
 * Model for the conflict management tool. Parses the documents.
 * Code in getParentFiles adapted from the jgit-cookbook
 */
public class ConflictManagementModel {
    private static final Logger logger = LogManager.getLogger();

    public ArrayList<ArrayList> parseConflicts(String fileName, String filePathWithoutFileName, String baseParent, String mergedParent){
        String path = filePathWithoutFileName + File.separator + fileName;
        ArrayList<String> base = getBaseParentFiles(fileName, baseParent);
        ArrayList<String> merged = getMergedParentFiles(fileName, mergedParent);
        ArrayList<ConflictLine> left = new ArrayList<>();
        ArrayList<ConflictLine> middle = new ArrayList<>();
        ArrayList<ConflictLine> right = new ArrayList<>();
        ConflictLine leftConflict = new ConflictLine(false);
        ConflictLine middleConflict = new ConflictLine(false);
        ConflictLine rightConflict = new ConflictLine(false);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));

            String line = reader.readLine();
            int leftCounter=0;
            int rightCounter=0;
            while(line!=null) {
                //System.out.println(" "+leftCounter+ " "+ rightCounter+" "+line+" "+base.get(leftCounter)+" "+merged.get(rightCounter));
                if(line.contains("<<<<<<<")) {
                    //line is the start of a conflict
                    left.add(leftConflict);
                    middle.add(middleConflict);
                    right.add(rightConflict);
                    line = reader.readLine();
                    leftConflict= new ConflictLine(true);
                    middleConflict= new ConflictLine(true);
                    rightConflict= new ConflictLine(true);
                    //left side of the conflict
                    while(!line.contains("=======")){
                        leftCounter++;
                        leftConflict.addLine(line);
                        line = reader.readLine();
                    }
                    line=reader.readLine();
                    //right side of the conflict
                    while(!line.contains(">>>>>>>")){
                        rightCounter++;
                        rightConflict.addLine(line);
                        line = reader.readLine();
                    }
                    left.add(leftConflict);
                    middle.add(middleConflict);
                    right.add(rightConflict);
                    leftConflict= new ConflictLine(false);
                    middleConflict= new ConflictLine(false);
                    rightConflict= new ConflictLine(false);
                    line = reader.readLine();
                } else if (changed(line, base.get(leftCounter), merged.get(rightCounter) )){
                    //line is part of a changed segment
                    if(!middleConflict.isChanged()) {
                        //if this is the first line of the change, switch to a new ConflictLine
                        left.add(leftConflict);
                        middle.add(middleConflict);
                        right.add(rightConflict);
                        leftConflict = new ConflictLine(false);
                        middleConflict = new ConflictLine(false);
                        rightConflict = new ConflictLine(false);
                        leftConflict.setChangedStatus(true);
                        middleConflict.setChangedStatus(true);
                        rightConflict.setChangedStatus(true);
                    }
                    middleConflict.addLine(line);
                    //detect whether the change comes from left or right
                    if(line.equals(base.get(leftCounter))){
                        leftConflict.addLine(line);
                        leftCounter++;
                    }
                    if(line.equals(merged.get(rightCounter))){
                        rightConflict.addLine(line);
                        rightCounter++;
                    }
                    line = reader.readLine();
                } else {
                    //line is the same across both originals
                    middleConflict.addLine(line);
                    leftConflict.addLine(line);
                    rightConflict.addLine(line);
                    rightCounter++;
                    leftCounter++;
                    line = reader.readLine();
                }
            }
            left.add(leftConflict);
            middle.add(middleConflict);
            right.add(rightConflict);
        } catch (Exception e){
            throw new ExceptionAdapter(e);
        }
        ArrayList<ArrayList> list = new ArrayList<>();
        list.add(left);
        list.add(middle);
        list.add(right);
        return list;
    }

    private ArrayList<String> getParentFiles(ObjectId parent, String fileName){
        Main.assertFxThread();
        try{
            Repository repository = SessionModel.getSessionModel().getCurrentRepoHelper().getRepo();
            System.out.println(repository);
            RevWalk revWalk = new RevWalk(repository);
            RevTree revTree = revWalk.parseCommit(parent).getTree();
            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(revTree);
            treeWalk.setRecursive(true);
            treeWalk.setFilter(PathFilter.create(fileName));
            System.out.println(treeWalk.next());
            if (!treeWalk.next()) {
                throw new IllegalStateException("Did not find expected file");
            }
            ObjectId objectId = treeWalk.getObjectId(0);

            ObjectLoader loader = repository.open(objectId);
            String string = new String(loader.getBytes());
            revWalk.dispose();
            return new  ArrayList<>(Arrays.asList(string.split("\n")));
        } catch (IOException e){
            throw new ExceptionAdapter(e);
        }
    }

    private ArrayList<String> getBaseParentFiles(String fileName, String base){
        Main.assertFxThread();
        ObjectId baseParent = ObjectId.fromString(base.substring(7,47));
        System.out.println(base);
        System.out.println(baseParent);
        return getParentFiles(baseParent, fileName);
    }

    private ArrayList<String> getMergedParentFiles(String fileName, String merged){
        Main.assertFxThread();
        ObjectId mergedParent = ObjectId.fromString(merged.substring(7,47));
        return getParentFiles(mergedParent, fileName);
    }

    private boolean changed(String line, String base, String merged){
        return (!line.equals(base) || !line.equals(merged));
    }
}



