import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import "./SubRoot.css";
import FolderIcon from "@mui/icons-material/Folder";
import DescriptionIcon from "@mui/icons-material/Description";
import { TreeItem } from "@mui/x-tree-view";
import { TreeView } from "@mui/x-tree-view/TreeView";
import { blue } from "@mui/material/colors";
import { BASE_URL } from "../../../utils/constants";
import ArrowDropDownIcon from "@mui/icons-material/ArrowDropDown";
import ArrowRightIcon from "@mui/icons-material/ArrowRight";
import { SET_LOADING } from "../../../redux/reducerSlice/authSlice";
import { CircularProgress } from "@mui/material";
import useAxiosPrivate from "../../../hooks/useAxiosPrivate";
import DataAndTime from "../../../common/DataAndTime";
import {
  add,
  addSubFolder,
} from "../../../redux/reducerSlice/folderAndFileSlice";

const SubNodes = ({ item, auditSetId, rootId, setSelectedItem }) => {
  const {
    itemId,
    itemName,
    itemType,
    createdAt,
    modifiedAt,
    modifiedBy,
    size,
  } = item;

  const [isLoading, setIsLoading] = useState(false);
  const [expandedNodes, setExpandedNodes] = useState([]);
  const axiosPrivate = useAxiosPrivate();

  const dispatch = useDispatch();

  const { subFolder } = useSelector((state) => state.folderAndFileSlice);
  const objectWithKey = subFolder.filter(
    (item) => item.key === `${rootId}-${itemId}`
  );

  let itemSubDetails = [];
  if (objectWithKey.length > 0) {
    itemSubDetails = objectWithKey[0].childrens;
    console.log(objectWithKey, "objectWithKey");
  }
  console.log("expandedNodes", subFolder, `${rootId}-${itemId}`, objectWithKey);

  const handleTreeItemClick = () => {
    setExpandedNodes((expanded) =>
      expanded.includes(itemId)
        ? expanded.filter((id) => id !== itemId)
        : [...expanded, itemId]
    );
  };

  useEffect(() => {
    if (isLoading) return;
    if (itemType === "file") return;
    if (itemSubDetails.length > 0) return;

    setIsLoading(true);

    axiosPrivate
      .get(
        `${BASE_URL}/box/auditSetItem/getItemFromAuditSet/${auditSetId}/${rootId}?subfolderId=${itemId}`
      )
      .then((response) => {
        console.log(response.data.data, "SUB");
        // setItemSubDetails(response.data.data);
        // dispatch(add(itemId, response.data.data));
        dispatch(addSubFolder(rootId, itemId, response.data.data));

        setIsLoading(false);
        // dispatch({ type: SET_LOADING, payload: false });
      })
      .catch((error) => {
        console.error(error);

        // dispatch({ type: SET_LOADING, payload: false });
      })
      .finally(() => {
        setIsLoading(false);
      });
  }, []);
  return (
    <div className="node-item ">
      {/* {isLoading && (
        <div className="loading-overlay">
          <CircularProgress className="loading-spinner" />
        </div>
      )} */}
      <TreeView
        aria-label="customized"
        // defaultExpanded={isLoaded ? ["1"] : []} // Expand if loaded
        defaultCollapseIcon={itemType === "file" ? null : <ArrowDropDownIcon />}
        defaultExpandIcon={itemType === "file" ? null : <ArrowRightIcon />}
        defaultEndIcon={
          isLoading ? (
            <div>
              <CircularProgress
                color="inherit"
                style={{
                  width: "12px",
                  height: "12px",
                  alignItems: "center",
                }}
              />
            </div>
          ) : itemType === "file" ? null : itemSubDetails.length >= 0 ? null : (
            <ArrowRightIcon />
          )
        }
        expanded={expandedNodes}
        onNodeToggle={(event, nodeIds) => setExpandedNodes(nodeIds)}
      >
        <TreeItem
          label={
            <div className="flex justify-between relative w-full ">
              <div className="flex ">
                {itemType === "file" ? (
                  <DescriptionIcon
                    sx={{ fontSize: "1.3rem", color: blue[700] }}
                  />
                ) : (
                  <FolderIcon sx={{ fontSize: "1.3rem", color: blue[700] }} />
                )}
                <p className="text-[13px]">{itemName}</p>
              </div>
              <div className="absolute z-10 right-60 w-30 ">
                {/* <p className="text-[13px]  ">{convertUtcToLocal(modifiedAt)}</p> */}
                <DataAndTime dataAndTime={modifiedAt} fontSize="14px" />
              </div>
              <div className="absolute z-10 right-40 w-15 ">
                <p className="text-[13px]  ">{size} </p>
              </div>
              <div className="absolute z-10 right-0 w-25 ">
                <p className="text-[13px] ">{modifiedBy}</p>
              </div>
            </div>
          }
          nodeId={itemId.toString()}
          onClick={(event) => {
            event.stopPropagation();
            if (expandedNodes.includes(itemId.toString())) {
              setExpandedNodes([]);
            } else {
              setExpandedNodes([itemId.toString()]);
            }
            setSelectedItem(item);
          }}
        >
          {itemSubDetails &&
            itemSubDetails.map((item, index) => {
              return (
                <SubNodes
                  key={index}
                  item={item}
                  auditSetId={auditSetId}
                  rootId={rootId}
                  setSelectedItem={setSelectedItem}
                />
              );
            })}
        </TreeItem>
      </TreeView>
    </div>
  );
};

export default SubNodes;
