import React, { useEffect, useMemo, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import FolderIcon from "@mui/icons-material/Folder";
import DescriptionIcon from "@mui/icons-material/Description";
import { TreeItem } from "@mui/x-tree-view";
import { blue } from "@mui/material/colors";
import { BASE_URL } from "../../../utils/constants";
import ArrowDropDownIcon from "@mui/icons-material/ArrowDropDown";
import ArrowRightIcon from "@mui/icons-material/ArrowRight";
import { TreeView } from "@mui/x-tree-view/TreeView";
import useAxiosPrivate from "../../../hooks/useAxiosPrivate";
import { CircularProgress } from "@mui/material";
import DataAndTime from "../../../common/DataAndTime";
import {
  add,
  addSubFolder,
} from "../../../redux/reducerSlice/folderAndFileSlice";

const SubInnerunderauditset = ({
  item,
  auditSetId,
  rootId,
  setSelectedItem,
}) => {
  const dispatch = useDispatch();
  const axiosPrivate = useAxiosPrivate();
  const {
    itemId,
    itemName,
    itemType,
    createdAt,
    modifiedAt,
    modifiedBy,
    size,
  } = item;

  const { subFolder } = useSelector((state) => state.folderAndFileSlice);
  const objectWithKey = subFolder.filter(
    (item) => item.key === `${rootId}-${itemId}`
  );

  let itemSubDetails = [];
  if (objectWithKey.length > 0) {
    itemSubDetails = objectWithKey[0].childrens;
  }
  const [expandedNodes, setExpandedNodes] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  console.log("expandedNodes", expandedNodes);

  const handleTreeItemNestedClick = () => {
    if (itemType === "file") return;
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

    console.log("length ", item.itemName, itemSubDetails.length);
    setIsLoading(true);
    axiosPrivate
      .get(
        `${BASE_URL}/box/auditSetItem/getItemFromAuditSet/${auditSetId}/${rootId}?subfolderId=${itemId}`
      )
      .then((response) => {
        console.log(response.data.data, "SUB");
        // dispatch(add(itemId, response.data.data));
        dispatch(addSubFolder(rootId, itemId, response.data.data));
      })
      .catch((error) => {
        console.error(error);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }, [auditSetId]);

  return (
    <div className="node-item ">
      {/* {isLoading && (
        <div className="loading-overlay">
          <CircularProgress className="loading-spinner" />
        </div>
      )} */}
      <TreeView
        aria-label="customized"
        defaultExpanded={["1"]}
        defaultCollapseIcon={itemType === "file" ? null : <ArrowDropDownIcon />}
        defaultExpandIcon={itemType === "file" ? null : <ArrowRightIcon />}
        defaultEndIcon={
          isLoading ? (
            <div>
              <CircularProgress
                color="inherit"
                style={{ width: "12px", height: "12px", alignItems: "center" }}
              />
            </div>
          ) : itemType === "file" ? null : itemSubDetails.length >= 0 ? null : (
            <ArrowRightIcon />
          )
        }
        expanded={expandedNodes}
        // onNodeToggle={(event, nodeIds) => setExpandedNodes(nodeIds)}
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
            // handleTreeItemNestedClick();
            if (expandedNodes.includes(itemId.toString())) {
              setExpandedNodes([]);
            } else {
              setExpandedNodes([itemId.toString()]);
            }
            setSelectedItem(item);
          }}
        >
          {/* {memoizedSubItems} */}
          {itemSubDetails &&
            itemSubDetails.map((item, index) => {
              return (
                <SubInnerunderauditset
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

export default SubInnerunderauditset;
