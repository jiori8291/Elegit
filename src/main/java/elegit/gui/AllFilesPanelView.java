package elegit.gui;

import elegit.Main;
import elegit.models.SessionModel;
import elegit.repofile.DirectoryRepoFile;
import elegit.repofile.RepoFile;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.util.Callback;
import net.jcip.annotations.ThreadSafe;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AllFilesPanelView displays all files in the current repository,
 * whether tracked or otherwise, as well as their status. It does so
 * in a hierarchical manner
 */
@ThreadSafe
// because of all the assert statements I have throughout. This is a view class, and at least for now,
// all methods must run on the FX thread. This class loses threadsafeness if any of that is changed.
// MOREOVER, there is extensive use of bindings and listeners here, which must happen on the FX thread.
public class AllFilesPanelView extends FileStructurePanelView {

//    private final Map<Path, TreeItem<RepoFile>> itemMap = new ConcurrentHashMap<>();

    public AllFilesPanelView() {
        Main.assertFxThread();
        init();
    }

    @Override
    public void init() {
        Main.assertFxThread();
//        itemMap.clear();
        super.init();
    }

    /**
     * @return a factory that generates a custom tree cell that includes a context menu for each
     * item
     */
    @Override
    protected Callback<TreeView<RepoFile>, TreeCell<RepoFile>> getTreeCellFactory() {
        Main.assertFxThread();
//        return arg -> new RepoFileTreeCell();
        return null;
    }

    @Override
    protected TreeItem<RepoFile> getRootTreeItem(DirectoryRepoFile rootDirectory) {
        Main.assertFxThread();
//        return new TreeItem<>(rootDirectory);
        return null;
    }

    /**
     * Builds a nested tree that follows the same file structure as the system, with the
     * base directory of the current repository as the root. Subsequent calls to this method
     * will update the items in place
     * @param repoFiles the files to add to the tree
     */
    @Override
    protected void addTreeItemsToRoot(List<RepoFile> repoFiles){
        // DRM: some of this will undoubtedly need to be threaded, but its safety needs to be carefully thought
        // given the FX work done. For now, insist in FX thread.
        Main.assertFxThread();
//
//        // To ensure files are added after their parents, sort the given files into lists
//        // based on their respective depths in the file structure
//        Map<Integer, List<RepoFile>> filesAtDepthMap = new HashMap<>();
//        int maxDepth = 0;
//        for(RepoFile repoFile : repoFiles){
//            int depthInRepo = repoFile.getLevelInRepository();
//
//            if(depthInRepo > maxDepth) maxDepth=depthInRepo;
//
//            if(!filesAtDepthMap.containsKey(depthInRepo)){
//                List<RepoFile> list = new LinkedList<>();
//                list.add(repoFile);
//                filesAtDepthMap.put(depthInRepo, list);
//            }else{
//                filesAtDepthMap.get(depthInRepo).add(repoFile);
//            }
//        }
//
//        // Track all currently displayed files to make sure they are still valid
//        Set<TreeItem<RepoFile>> itemsToRemove = new HashSet<>();
//        itemsToRemove.addAll(itemMap.values());
//
//        // Loop through files at each depth
//        for(int i = 0; i < maxDepth + 1; i++) {
//            List<RepoFile> filesAtDepth = filesAtDepthMap.get(i);
//            if(filesAtDepth != null) {
//                for (RepoFile repoFile : filesAtDepth) {
//                    Path pathToFile = repoFile.getFilePath();
//
//                    // Check if there is already a record of this file
//                    if (itemMap.containsKey(pathToFile)) {
//                        TreeItem<RepoFile> oldItem = itemMap.get(pathToFile);
//
//                        if (oldItem.getValue().equals(repoFile)) {
//                            // The given file is already present, no additional processing needed
//                            itemsToRemove.remove(oldItem);
//                        } else {
//                            // The file is displayed, but needs its status updated. Replace the old with the new
//                            CheckBoxTreeItem<RepoFile> newItem = new CheckBoxTreeItem<>(repoFile, repoFile.getDiffButton());
//                            TreeItem<RepoFile> parent = oldItem.getParent();
//                            newItem.setExpanded(oldItem.isExpanded());
//                            newItem.getChildren().setAll(oldItem.getChildren());
//                            parent.getChildren().set(parent.getChildren().indexOf(oldItem), newItem);
//                            itemsToRemove.remove(oldItem);
//                            itemMap.put(pathToFile, newItem);
//                        }
//                    } else {
//                        // The given file wasn't present, so need to add it
//                        CheckBoxTreeItem<RepoFile> newItem = new CheckBoxTreeItem<>(repoFile, repoFile.getDiffButton());
//
//                        Path pathToParent = pathToFile.getParent();
//                        boolean foundParent = false;
//                        // Make sure this new item is properly inserted as a child to its parent
//                        while (pathToParent != null && !root.getValue().getFilePath().equals(pathToParent)) {
//                            if (itemMap.containsKey(pathToParent)) {
//                                TreeItem<RepoFile> parent = itemMap.get(pathToParent);
//                                //Platform.runLater(() ->
//                                        parent.getChildren().add(newItem);
//                                //);
//                                foundParent = true;
//                                break;
//                            }
//                            pathToParent = pathToParent.getParent();
//                        }
//                        // If no parent is found, we can assume it belongs to the root
//                        if (!foundParent) {
//                            root.getChildren().add(newItem);
//                        }
//                        itemMap.put(pathToFile, newItem);
//                        itemsToRemove.remove(newItem);
//                    }
//                }
//            }
//        }
//
//        // Remove all elements that shouldn't be displayed
//        for (TreeItem<RepoFile> item : itemsToRemove) {
//            //Platform.runLater(() -> {
//                if(item.getParent() != null) item.getParent().getChildren().remove(item);
//            //});
//            itemMap.remove(item.getValue().getFilePath());
//        }
    }

    /**
     * @return every file in the repository (included untracked, ignored, etc)
     * @throws GitAPIException
     * @throws IOException
     */
    @Override
    public List<RepoFile> getFilesToDisplay() throws GitAPIException, IOException {
        Main.assertFxThread();
//        return SessionModel.getSessionModel().getAllRepoFiles();
        return null;
    }

    /**
     * An overwritten version of TreeCell that adds a context menu to our
     * tree structure
     */
    private class RepoFileTreeCell extends CheckBoxTreeCell<RepoFile> {
        @Override
        public void updateItem(RepoFile item, boolean empty){
            Main.assertFxThread();
            super.updateItem(item, empty);

            setText(getItem() == null ? "" : getItem().toString());

            setOnContextMenuRequested(event -> {
                if(getTreeItem() != null) getTreeItem().getValue().showContextMenu(this, event.getScreenX(), event.getScreenY());
            });
        }
    }
}
