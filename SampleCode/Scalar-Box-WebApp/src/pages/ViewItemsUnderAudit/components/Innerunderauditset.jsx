import React, { useEffect, useMemo, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import FolderIcon from "@mui/icons-material/Folder";
import DescriptionIcon from "@mui/icons-material/Description";
import ArrowDropDownIcon from "@mui/icons-material/ArrowDropDown";
import ArrowRightIcon from "@mui/icons-material/ArrowRight";
import { TreeView } from "@mui/x-tree-view/TreeView";
import { TreeItem } from "@mui/x-tree-view/TreeItem";
import { blue } from "@mui/material/colors";
import SubInnerunderauditset from "./SubInnerunderauditset";
import { BASE_URL } from "../../../utils/constants";
import { CircularProgress } from "@mui/material";
import useAxiosPrivate from "../../../hooks/useAxiosPrivate";

import DataAndTime from "../../../common/DataAndTime";
import { add } from "../../../redux/reducerSlice/folderAndFileSlice";

const Innerunderauditset = ({ item, auditSetId, rootId, setSelectedItem }) => {
  const { itemId, itemName, itemType, modifiedAt, size, modifiedBy } = item;
  const dispatch = useDispatch();
  const axiosPrivate = useAxiosPrivate();

  const { data } = useSelector((state) => state.folderAndFileSlice);
  const objectWithKey = data.filter((item) => item.rootId === itemId);

  let itemSubDetails = [];
  if (objectWithKey.length > 0) {
    itemSubDetails = objectWithKey[0].childrens;
    console.log(objectWithKey, "objectWithKey");
    // itemSubDetails = objectWithKey.childrens
  }
  console.log("DATA :: Innerunderauditset ", itemSubDetails, data, itemName);
  // const [itemSubDetails, setItemSubDetails] = useState([]);
  const [expandedNodes, setExpandedNodes] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  // console.log("USE MEMO :: ", memoizedSubItems);

  const handleItemClick = () => {
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

    console.log(
      "useEffect Innerunderauditset ",
      item.itemName,
      itemSubDetails.length
    );

    setIsLoading(true);

    axiosPrivate
      .get(
        `${BASE_URL}/box/auditSetItem/getItemFromAuditSet/${auditSetId}/${encodeURIComponent(
          itemId
        )}`
      )
      .then((response) => {
        dispatch(add(itemId, response.data.data));
        // setItemSubDetails(response.data.data);
        // setIsLoaded(true);
        // setIsLoading(false);
        console.log(response.data.data, "RESPONSE");
      })
      .catch((error) => {
        // setIsLoading(false);
        // console.error(error);
      })
      .finally(() => {
        setIsLoading(false);
      });
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
              <div
                className="flex "
                style={{
                  alignItems: "center",
                }}
              >
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
                {/* <p className="text-[13px]  ">
                  {convertUtcToLocal(modifiedAt)}
                  
                </p> */}
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
              console.log("SubInnerunderauditset", item);
              // return <>sdfsd</>;
              return (
                <SubInnerunderauditset
                  key={item.itemId}
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

export default Innerunderauditset;
