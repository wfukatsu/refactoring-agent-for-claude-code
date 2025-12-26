import React, { useEffect, useState } from "react";
import { useSelector, useDispatch } from "react-redux";
import SubNodes from "./SubNodes";
import "./Root.css";
import FolderIcon from "@mui/icons-material/Folder";
import DescriptionIcon from "@mui/icons-material/Description";
import ArrowDropDownIcon from "@mui/icons-material/ArrowDropDown";
import ArrowRightIcon from "@mui/icons-material/ArrowRight";
import { TreeView } from "@mui/x-tree-view/TreeView";
import { TreeItem } from "@mui/x-tree-view/TreeItem";
import { blue } from "@mui/material/colors";
import { BASE_URL } from "../../../utils/constants";
import { CircularProgress } from "@mui/material";
import DataAndTime from "../../../common/DataAndTime";
import { add } from "../../../redux/reducerSlice/folderAndFileSlice";
import useAxiosPrivate from "../../../hooks/useAxiosPrivate";

const Node = ({ item, auditSetId, rootId, setSelectedItem }) => {
  const { itemId, itemName, itemType, modifiedAt, size, modifiedBy } = item;
  const [isLoading, setIsLoading] = useState(false);
  const [expandedNodes, setExpandedNodes] = useState([]);
  const axiosPrivate = useAxiosPrivate();
  const dispatch = useDispatch();

  const { data } = useSelector((state) => state.folderAndFileSlice);
  const objectWithKey = data.filter((item) => item.rootId === itemId);

  let itemSubDetails = [];
  if (objectWithKey.length > 0) {
    itemSubDetails = objectWithKey[0].childrens;
    console.log(objectWithKey, "objectWithKey");
  }

  const handleItemClick = () => {
    setExpandedNodes((expanded) =>
      expanded.includes(itemId)
        ? expanded.filter((id) => id !== itemId)
        : [...expanded, itemId]
    );
  };

  useEffect(() => {
    const fetchItems = () => {
      if (isLoading) return;
      if (itemType === "file") return;
      if (itemSubDetails.length > 0) return;

      // setIsLoading(true);
      setIsLoading(true);

      axiosPrivate
        .get(
          `${BASE_URL}/box/auditSetItem/getItemFromAuditSet/${auditSetId}/${encodeURIComponent(
            itemId
          )}`
        )
        .then((response) => {
          dispatch(add(itemId, response.data.data));
          console.log(response.data.data, "RESPONSE");
        })
        .catch((error) => {
          console.error(error);
        })
        .finally(() => {
          setIsLoading(false);
        });
    };

    console.log("????????????????????111111111111")

    fetchItems();
  }, [auditSetId, itemId]);

  return (
    <>
      {/* {isLoading && (
        <div className="loading-overlay">
          <CircularProgress className="loading-spinner" />
        </div>
      )} */}
      <TreeView
        aria-label="customized"
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
        onNodeToggle={(event, nodeIds) => setExpandedNodes(nodeIds)}
      >
        <TreeItem
          nodeId={itemId.toString()}
          label={
            <div className="flex justify-between relative w-full ">
              <div className="flex " style={{ alignItems: "center" }}>
                {itemType === "file" ? (
                  <DescriptionIcon
                    sx={{ fontSize: "1.3rem", color: blue[700] }}
                  />
                ) : (
                  <FolderIcon sx={{ fontSize: "1.3rem", color: blue[700] }} />
                )}
                <p className="text-[13px] pt-1">{itemName}</p>
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
          onClick={(event) => {
            event.stopPropagation();
            handleItemClick();
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
    </>
  );
};

export default Node;
