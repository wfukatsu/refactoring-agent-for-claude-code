/* eslint-disable react/prop-types */
import "./Root.css";
import { useEffect, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import FileIcon from "../../Icons/FileIcon";
import FolderIcon from "../../Icons/FolderIcon";
import { BASE_URL, intersection1 } from "../../utils/constant";
import { Checkbox, FormControlLabel } from "@mui/material";
import { newaddToDenaiList, newremoveFromDenaiList } from "./auditFolderSlice";
import DescriptionOutlinedIcon from "@mui/icons-material/DescriptionOutlined";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";

export default function NodeBottomUp({
  item,
  setSubChild,
  isRootAllowed,
  isRoot,
  removeRoot,
  parentItem,
  rootsItems,
  removeRootCallBack,
}) {
  const { user } = useSelector((store) => store.auth);

  const [itemDetails, setItemdetails] = useState([]);

  const refSet = useRef(new Map());

  const dispatch = useDispatch();
  const axiosPrivate = useAxiosPrivate();

  const { allowedList, selectedAuditSet } = useSelector(
    (store) => store.folder
  );
  //   console.log("removeRootMap :: ", item, removeRootMap);

  const allowedListMap = new Map();

  [...allowedList].forEach((item) => {
    allowedListMap.set(item.id, item);
  });
  // const isRootAllowed = allowedListMap.has(itemdata.id);

  const deniedSet = new Set(allowedList);

  // const isDenied = deniedSet.has(item);

  const allowed = intersection1(new Set([...itemDetails]), deniedSet); // deniedSet.intersection(itemDetails);

  const isAllowed = isRootAllowed && allowed.size === itemDetails.length;
  let isDenied =
    isRootAllowed && itemDetails.length === 0 ? true : allowed.size !== 0;

  //   if (item.id === 248804255060) {
  //     console.log("ALLOWED**************", allowed);
  //     console.log(
  //       "allowedList",
  //       allowedList,
  //       "Allowed  SIZE:: ",
  //       allowed.size,
  //       "ITEMS SIZE :: ",
  //       itemDetails.length
  //     );

  //     console.log("isAllowed :: ", isAllowed, "isDenied :: ", isDenied);
  //   }
  let checked = [isAllowed, isDenied];

  //   if (allowed.size === 1) {
  //     removeRootMap.set(item.id, { ...item });
  //     console.log("ALLOWED*** counter inside ", stepp++, removeRootMap);
  //   }

  const removeFolderHandler = (removeMap) => {
    removeRootCallBack(removeMap);
    if (isRoot) {
      const list = [];
      removeMap.forEach((value, key) => {
        list.push({ ...value });
      });
      console.log(
        "Allo    ELSE   FOLDER IF ROOT removeRootCallBackFun ",
        item.id
      );
      dispatch(newremoveFromDenaiList(list));
    } else {
      removeRootCallBack(removeMap);
    }
  };

  const removeRootCallBackFun = (removeMap) => {
    if (item.type === "folder") {
      if (allowed.size === 1 || allowed.size === 0) {
        removeMap.set(item.id, item);
        // removeRootCallBack(removeMap);
        console.log("Allo    ELSE   FOLDER IF removeRootCallBackFun ", item.id);

        if (isRoot) {
          const list = [];
          removeMap.forEach((value, key) => {
            list.push({ ...value });
          });
          console.log(
            "Allo    ELSE   FOLDER IF ROOT removeRootCallBackFun ",
            item.id
          );
          dispatch(newremoveFromDenaiList(list));
        } else {
          removeRootCallBack(removeMap);
        }
      } else {
        console.log("Allo    ELSE   FOLDER ALLOWED ", allowed.size);
        const list = [];
        removeMap.forEach((value, key) => {
          list.push({ ...value });
        });
        console.log("Allo    ELSE   FOLDER ELSE ********** ", item.id);

        dispatch(newremoveFromDenaiList(list));
      }
    } else if (item.type === "file") {
      removeMap.set(item.id, item);
      removeRootCallBack(removeMap);
      console.log("Allo    ELSE  FILE removeRootCallBackFun ", item.id);

      //   dispatch(newremoveFromDenaiList([{ ...parentItem }]));
    }
  };

  const subChild = (subChildList) => {
    [...subChildList].forEach((item) => {
      refSet.current.set(item.id, item);
    });

    console.log("FOLDER NAME :: ", item.name, "REF ::", refSet.current);
    let list = [];
    refSet.current.forEach((item) => {
      list.push(item);
    });

    itemDetails.forEach((item) => {
      list.push(item);
    });
    setSubChild(list);
  };

  async function fetchItem() {
    if (item.type === "file") {
      return;
    }
    console.log("before fetchItem");
    if (itemDetails.length <= 0) {
      console.log("fetchItem", item.id, user.jwtToken);
      // const headers = new Headers();
      // headers.append("Content-Type", "application/json");
      // headers.append(
      //   "Authorization",
      //   `Bearer ${encodeURIComponent(user.jwtToken)}`
      // );

      const url = `${BASE_URL}/box/folder/getItemList?folderId=${encodeURIComponent(
        item.id
      )}`;

      const response = await axiosPrivate.get(url);
      // const response = await fetch(url, {
      //   method: "GET",
      //   headers: headers,
      // });

      if (response.status === 200) {
        const { folderDetailsDtoList } = { ...response.data.data };
        // const mapData = new Map();
        // [...folderDetailsDtoList].map((item) => {
        //   mapData[item.id] = item;
        // });
        // setItemdetails(mapData);
        setItemdetails([...folderDetailsDtoList]);
        setSubChild([...folderDetailsDtoList]);
        if (isRootAllowed && selectedAuditSet === "null" && !isRoot) {
          dispatch(newaddToDenaiList([...folderDetailsDtoList]));
        }
      }
    }
  }

  useEffect(() => {
    async function initialFetchItem() {
      if (item.type === "file") {
        return;
      }
      console.log("before fetchItem");
      if (itemDetails.length <= 0) {
        console.log("fetchItem", item.id, user.jwtToken);
        // const headers = new Headers();
        // headers.append("Content-Type", "application/json");
        // headers.append(
        //   "Authorization",
        //   `Bearer ${encodeURIComponent(user.jwtToken)}`
        // );

        const url = `${BASE_URL}/box/folder/getItemList?folderId=${encodeURIComponent(
          item.id
        )}`;
        const response = await axiosPrivate.get(url);
        // const response = await fetch(url, {
        //   method: "GET",
        //   headers: headers,
        // });

        if (response.status === 200) {
          const { folderDetailsDtoList } = { ...response.data.data };
          // const mapData = new Map();
          // [...folderDetailsDtoList].map((item) => {
          //   mapData[item.id] = item;
          // });
          // setItemdetails(mapData);
          setItemdetails([...folderDetailsDtoList]);
          setSubChild([...folderDetailsDtoList]);
          // if (isRootAllowed && selectedAuditSet === "null" && !isRoot) {
          //   dispatch(newaddToDenaiList([...folderDetailsDtoList]));
          // }
        }
      }
    }

    initialFetchItem();
  }, [
    dispatch,
    isRoot,
    isRootAllowed,
    item.id,
    item.type,
    itemDetails.length,
    user.jwtToken,
  ]);

  return (
    <>
      <div className="node-item">
        {item.type === "folder" ? (
          <FolderIcon className="folder" style={{ color: "#0061D5" }} />
        ) : (
          // <FileIcon className="folder" />
          // <DescriptionOutlinedIcon sx={{ color: "#0061D5" }} />
          <div className="folder">
            <DescriptionOutlinedIcon sx={{ color: "#0061D5" }} />
          </div>
        )}
        {item.type === "folder" ? (
          <details open={false} onClick={fetchItem}>
            <summary className="">
              <FormControlLabel
                sx={{ marginLeft: "1px" }}
                label={`${item.name}`}
                control={
                  <Checkbox
                    // sx={{
                    //   "& .MuiSvgIcon-root": {
                    //     fontSize: 22,
                    //   },
                    // }}
                    // checked={isDenied}
                    checked={checked[0] && checked[1]}
                    indeterminate={checked[0] !== checked[1]}
                    onChange={(e) => {
                      let list = [];

                      list.push({ ...item });

                      itemDetails.forEach((i) => {
                        list.push({ ...i });
                      });

                      if (e.target.checked) {
                        refSet.current.forEach((i) => {
                          list.push({ ...i });
                        });

                        list = [...list, ...rootsItems];

                        dispatch(newaddToDenaiList(list));
                      } else {
                        //if (removeRoot) {
                        //list.push({ ...parentItem });
                        //   removeRootMap.forEach((value, key) => {
                        //     list.push({ ...value });
                        //   });
                        //}

                        const removeMap = new Map();

                        refSet.current.forEach((i) => {
                          list.push({ ...i });
                        });
                        list.forEach((i) => {
                          removeMap.set(i.id, i);
                        });
                        removeFolderHandler(removeMap);
                        // removeRootCallBackFun(removeMap);
                        // dispatch(newremoveFromDenaiList(list));
                      }
                      //   if (isRoot) {
                      //     if (e.target.checked) {
                      //       dispatch(newaddToDenaiList(list));
                      //     } else {
                      //       if (removeRoot) {
                      //         list.push(parentItem);
                      //       }
                      //       dispatch(newremoveFromDenaiList(list));
                      //     }
                      //   } else {
                      //     if (!allowedListMap.has(parentItem.id)) return;

                      //     if (e.target.checked) {
                      //       dispatch(newaddToDenaiList(list));
                      //     } else {
                      //       if (removeRoot) {
                      //         list.push({ id: parentItem.id });
                      //       }
                      //       dispatch(newremoveFromDenaiList(list));
                      //     }
                      //   }
                    }}
                  />
                }
                onClick={(e) => e.stopPropagation()}
              />
            </summary>

            {itemDetails && (
              <div className="align-right">
                <ul>
                  {[...itemDetails].map((i, index) => {
                    return (
                      <li key={index}>
                        <NodeBottomUp
                          item={i}
                          setSubChild={subChild}
                          isRootAllowed={allowedListMap.has(i.id)}
                          isRoot={false}
                          removeRoot={allowed.size === 1}
                          parentItem={item}
                          rootsItems={[...rootsItems, { ...item }]}
                          //   removeRootMap={removeRootMap}
                          removeRootCallBack={removeRootCallBackFun}
                        />
                        {/* <RootTest
                          item={i}
                          setSubChild={subChild}
                          isRootAllowed={allowedListMap.has(i.id)}
                          isRoot={false}
                          removeRoot={allowed.size === 1}
                          parentItem={item}
                        /> */}
                      </li>
                    );
                  })}
                </ul>
              </div>
            )}
          </details>
        ) : (
          <FormControlLabel
            label={`${item.name}`}
            control={
              <Checkbox
                checked={isAllowed}
                onChange={(e) => {
                  //   let listMap = new Map();
                  //   list.push({ ...item });
                  //   listMap.set(item.id, item);

                  if (e.target.checked) {
                    dispatch(newaddToDenaiList([...rootsItems, { ...item }]));
                  } else {
                    //if (removeRoot) {
                    //list.push({ ...parentItem });
                    //listMap.set(parentItem.id, parentItem);
                    //   removeRootMap.forEach((value, key) => {
                    //     listMap.set(value.id, value);
                    //     // list.push({ ...value });
                    //   });
                    //}

                    // const list = [];
                    // listMap.forEach((value, key) => {
                    //   list.push({ ...value });
                    // });
                    // console.log(
                    //   "Allo    ELSE",
                    //   removeRoot,
                    //   "==",
                    //   //   removeRootMap,
                    //   list
                    // );

                    //dispatch(newremoveFromDenaiList([...list]));
                    removeRootCallBackFun(new Map());
                  }

                  //   if (isRoot) {
                  //     if (e.target.checked) {
                  //       dispatch(newaddToDenaiList(list));
                  //     } else {
                  //       // if (removeRoot) {
                  //       //   list.push({ id: item.id });
                  //       // }
                  //       dispatch(newremoveFromDenaiList(list));
                  //     }
                  //   } else {
                  //     // if (!allowedListMap.has(parentItem.id)) return;

                  //     if (e.target.checked) {
                  //       dispatch(newaddToDenaiList(list));
                  //     } else {
                  //       console.log(
                  //         "ALLOWED****B",
                  //         removeRoot,
                  //         allowedList,
                  //         item.id,
                  //         parentItem.id
                  //       );
                  //       if (removeRoot) {
                  //         list.push({ id: parentItem.id });
                  //       }
                  //       dispatch(newremoveFromDenaiList(list));
                  //     }
                  //   }
                }}
              />
            }
          />
        )}
      </div>
    </>
  );
}
